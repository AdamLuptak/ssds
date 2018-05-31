package com.here.owc.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmrErrorMessage.class, name = "emrErrorMessage"),
        @JsonSubTypes.Type(value = EmrInfoMessage.class, name = "emrInfoMessage") })
public abstract class EmrMessage implements Serializable {

    private PlaceOfRevelation placeOfRevelation;
    private String notes;

    public EmrMessage(PlaceOfRevelation placeOfRevelation, String notes) {
        this.placeOfRevelation = placeOfRevelation;
        this.notes = notes;
    }

    public EmrMessage() {

    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public PlaceOfRevelation getPlaceOfRevelation() {
        return placeOfRevelation;
    }

    public void setPlaceOfRevelation(PlaceOfRevelation placeOfRevelation) {
        this.placeOfRevelation = placeOfRevelation;
    }

}
