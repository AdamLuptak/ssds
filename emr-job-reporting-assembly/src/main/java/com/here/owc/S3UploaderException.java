package com.here.owc;

public class S3UploaderException extends Exception {

    private static final long serialVersionUID = 1L;

    public S3UploaderException(Exception e) {
        super(e);
    }

    public S3UploaderException(String message) {
        super(message);
    }
}
