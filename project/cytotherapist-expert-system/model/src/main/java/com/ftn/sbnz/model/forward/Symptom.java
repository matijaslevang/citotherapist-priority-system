package com.ftn.sbnz.model.forward;

public class Symptom {
    private String type; // "BOLOVI", "TEGOBE"

    public Symptom(String type) {
        this.type = type;
    }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}