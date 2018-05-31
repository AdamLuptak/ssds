package com.here.owc;

import java.sql.SQLException;

public class DatabaseAdminException extends Exception {

    private static final long serialVersionUID = 1L;

    public DatabaseAdminException(String msg, SQLException e) {
        super(msg, e);
    }
}
