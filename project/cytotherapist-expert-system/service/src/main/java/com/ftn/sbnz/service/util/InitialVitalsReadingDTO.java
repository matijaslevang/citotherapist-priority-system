package com.ftn.sbnz.service.util;

public class InitialVitalsReadingDTO {
    private Double temperature;
    private Integer upperPressure;
    private Integer lowerPressure;
    private Boolean hadPainComplaints;
    private Boolean hadMedicalIssues;

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getUpperPressure() {
        return upperPressure;
    }

    public void setUpperPressure(Integer upperPressure) {
        this.upperPressure = upperPressure;
    }

    public Integer getLowerPressure() {
        return lowerPressure;
    }

    public void setLowerPressure(Integer lowerPressure) {
        this.lowerPressure = lowerPressure;
    }

    public Boolean getHadPainComplaints() {
        return hadPainComplaints;
    }

    public void setHadPainComplaints(Boolean hadPainComplaints) {
        this.hadPainComplaints = hadPainComplaints;
    }

    public Boolean getHadMedicalIssues() {
        return hadMedicalIssues;
    }

    public void setHadMedicalIssues(Boolean hadMedicalIssues) {
        this.hadMedicalIssues = hadMedicalIssues;
    }
}
