package com.ftn.sbnz.model.forward;

public class TemperatureAssessment {
    private String status;

    public TemperatureAssessment(String status) {
        this.status = status;
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}