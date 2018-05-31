package com.here.owc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.exception.ExceptionUtils;

public class EmrErrorMessage extends EmrMessage {

    private Exception exception;
    private String notes;

    public EmrErrorMessage() {
        super();
    }

    public EmrErrorMessage(PlaceOfRevelation placeOfRevelation, Exception exception, String notes) {
        super(placeOfRevelation, notes);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @JsonIgnore
    public String getExceptionStackTrace() {
        return ExceptionUtils.getStackTrace(exception);
    }

}
