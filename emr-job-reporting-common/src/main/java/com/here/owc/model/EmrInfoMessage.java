package com.here.owc.model;

public class EmrInfoMessage extends EmrMessage {
    String message;

    public EmrInfoMessage() {
        super();
    }

    public EmrInfoMessage(PlaceOfRevelation placeOfRevelation, String message, String notes) {
        super(placeOfRevelation, notes);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
