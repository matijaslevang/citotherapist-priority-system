package com.ftn.sbnz.model.template;

public class ProtocolInformation {
    private String prepAmpouleName;
    private Double prepAmpouleAmount;
    private String prepAmpouleTime;
    private String medicineName;
    private Double medicineAmount;
    private Double medicineSolventAmount;
    private String medicineTime;

    public String getPrepAmpouleName() {
        return prepAmpouleName;
    }

    public void setPrepAmpouleName(String prepAmpouleName) {
        this.prepAmpouleName = prepAmpouleName;
    }

    public Double getPrepAmpouleAmount() {
        return prepAmpouleAmount;
    }

    public void setPrepAmpouleAmount(Double prepAmpouleAmount) {
        this.prepAmpouleAmount = prepAmpouleAmount;
    }

    public String getPrepAmpouleTime() {
        return prepAmpouleTime;
    }

    public void setPrepAmpouleTime(String prepAmpouleTime) {
        this.prepAmpouleTime = prepAmpouleTime;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public Double getMedicineAmount() {
        return medicineAmount;
    }

    public void setMedicineAmount(Double medicineAmount) {
        this.medicineAmount = medicineAmount;
    }

    public Double getMedicineSolventAmount() {
        return medicineSolventAmount;
    }

    public void setMedicineSolventAmount(Double medicineSolventAmount) {
        this.medicineSolventAmount = medicineSolventAmount;
    }

    public String getMedicineTime() {
        return medicineTime;
    }

    public void setMedicineTime(String medicineTime) {
        this.medicineTime = medicineTime;
    }
}
