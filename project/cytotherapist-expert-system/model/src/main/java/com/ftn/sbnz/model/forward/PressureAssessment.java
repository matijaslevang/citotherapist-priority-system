package com.ftn.sbnz.model.forward;

public class PressureAssessment {
    private String status;

    public PressureAssessment(String status) {
        this.status = status;
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}