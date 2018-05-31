package com.here.owc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

/**
 * Admin class for database manipulation.
 */
public class DatabaseAdmin {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAdmin.class);
    private static final String REVOKE_ALL = "REVOKE ALL ON DATABASE ${dbname} FROM ${newUser} CASCADE;";
    private static final String DROP_SCHEMA = "DROP SCHEMA ${newUser} CASCADE;";
    private static final String DROP_USER = "DROP USER ${newUser};";
    private static final String CREATE_USER = "CREATE USER ${newUser} WITH PASSWORD '${newUserPassword}';";
    private static final String GRANT_ALL = "CREATE SCHEMA ${newUser};";
    private static final String CREATE_SCHEMA = "GRANT ALL PRIVILEGES ON SCHEMA ${newUser} TO ${newUser}";
    private static final String PREVENT_SQL_INJECTION_REGEX = "[\\w_]*";

    private String dbUrl;
    private String dbAdminUser;
    private String dbAdminPassword;
    private String dbName;

    public DatabaseAdmin(DatabaseAdminConf conf) {
        this.dbUrl = conf.getDbUrl();
        this.dbAdminUser = conf.getDbAdminUser();
        this.dbAdminPassword = conf.getDbAdminPassword();
        this.dbName = conf.getDbName();
    }

    private String sanitizeParameter(String argument) {
        if (!Pattern.compile(PREVENT_SQL_INJECTION_REGEX).matcher(argument).matches()) {
            throw new IllegalArgumentException(String.format("Argument: [ %s ] contains illegal character", argument));
        }
        return argument;
    }

    /**
     * Drop create user and schema witch is connected with user.
     * @param newUser
     * @param newUserPassword
     */
    public void dropCreateUserAndSchema(String newUser, String newUserPassword) throws DatabaseAdminException {
        String cleanedNewUserName = sanitizeParameter(newUser);
        String cleanedNewUserPassword = sanitizeParameter(newUserPassword);
        String dropQuery = createDeleteQuery(cleanedNewUserName);
        String createQuery = createNewUserSchemaQuery(cleanedNewUserName, cleanedNewUserPassword);

        try (Connection conn = DriverManager.getConnection(dbUrl, dbAdminUser, dbAdminPassword);
             Statement st = conn.createStatement()) {

            dropUserAndSchemaIfExist(dropQuery, st);
            st.execute(createQuery);

        } catch (SQLException e) {
            throw new DatabaseAdminException("Creation of new user fail cause", e);
        }
        String msg = String.format("New user: %s with password: %s with schema: %s", cleanedNewUserName,
                cleanedNewUserPassword,
                cleanedNewUserName);
        logger.info(msg);
    }

    private void dropUserAndSchemaIfExist(String dropQuery, Statement st) {
        try {
            st.execute(dropQuery);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String createNewUserSchemaQuery(String newUser, String newUserPassword) {
        String createQuery = CREATE_USER + GRANT_ALL + CREATE_SCHEMA;
        createQuery = replacePlaceholderInQuery(createQuery, "dbname", dbName);
        createQuery = replacePlaceholderInQuery(createQuery, "newUser", newUser);
            createQuery = replacePlaceholderInQuery(createQuery, "newUserPassword", newUserPassword);
        return createQuery;
    }

    private String replacePlaceholderInQuery(String createQuery, final String placeholderName,
            String value) {
        String regex = String.format("\\$\\{%s\\}", placeholderName);
        return createQuery.replaceAll(regex, value);
    }

    private String createDeleteQuery(String newUser) {
        String dropQuery = REVOKE_ALL + DROP_SCHEMA + DROP_USER;
        dropQuery = replacePlaceholderInQuery(dropQuery, "newUser", newUser);
        dropQuery = replacePlaceholderInQuery(dropQuery, "dbname", dbName);
        return dropQuery;
    }
}
