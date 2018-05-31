package com.here.owc;

import org.flywaydb.commandline.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Runner for flyway migration and optionally can drop create schema with user.
 */
public final class DatabaseMigrationManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationManager.class);
    private static final List<String> DB_ADMIN_ARGS = Arrays.asList("initSchema");

    private DatabaseMigrationManager() {

    }

    /**
     * Run run Admin operation and than call flyway commandline
     * @param args
     */
    public static void main(String[] args) {
        try {
            runAdminOperation(args);

            String[] flywayArgs = excludeFromArray(DB_ADMIN_ARGS, args);

            callFlywayCommandLine(flywayArgs);
        } catch (IOException | DatabaseAdminException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void runAdminOperation(String[] args)
            throws IOException, DatabaseAdminException, ClassNotFoundException {
        if (isResetSchemaOn(args)) {
            logger.info("Running 'initSchema' command");
            final String propertyFilePath = "C:\\Users\\luptak\\git\\emrjobreporting\\emr-job-schema\\src\\main\\scripts\\flyway\\conf\\flyway.conf";
            Properties prop = loadProperties(propertyFilePath);
            final String newUser = prop.getProperty("flyway.user");
            final String newUserPassword = prop.getProperty("flyway.password");
            final String driver = prop.getProperty("flyway.driver");

            Class.forName(driver);

            DatabaseAdminConf adminConf = new DatabaseAdminConf(prop);
            DatabaseAdmin databaseAdmin = new DatabaseAdmin(adminConf);
            databaseAdmin.dropCreateUserAndSchema(newUser, newUserPassword);
        }
    }

    private static boolean isResetSchemaOn(String[] args) {
        return Arrays.stream(args).anyMatch(DB_ADMIN_ARGS::contains);
    }

    private static String[] excludeFromArray(List<String> excludeList, String[] args) {
        return Arrays.stream(args).filter(a -> !excludeList.contains(a)).toArray(String[]::new);
    }

    private static void callFlywayCommandLine(String[] args) {
        Main.main(args);
    }

    private static Properties loadProperties(String propertiesFilePath) throws IOException {
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            Properties prop = new Properties();
            prop.load(input);
            return prop;
        }
    }

}
