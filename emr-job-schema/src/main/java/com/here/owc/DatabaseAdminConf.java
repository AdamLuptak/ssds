package com.here.owc;

import java.util.Properties;

public class DatabaseAdminConf {

    private String dbUrl;
    private String dbAdminUser;
    private String dbAdminPassword;
    private String dbName;

    public DatabaseAdminConf(Properties prop) {
        this.dbUrl = prop.getProperty("flyway.url");
        this.dbAdminUser = prop.getProperty("db_admin_user");
        this.dbAdminPassword = prop.getProperty("db_admin_password");
        this.dbName = prop.getProperty("db_name");
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbAdminUser() {
        return dbAdminUser;
    }

    public String getDbAdminPassword() {
        return dbAdminPassword;
    }

    public String getDbName() {
        return dbName;
    }
}
