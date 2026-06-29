package com.ftn.sbnz.service.util;

public class ProtocolInformationDTO {
    private String prepAmpouleName;
    private Double prepAmpouleAmount;
    private Double prepAmpouleTime;
    private String medicineName;
    private Double medicineAmount;
    private Double medicineSolventAmount;
    private Double medicineTime;

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

    public Double getPrepAmpouleTime() {
        return prepAmpouleTime;
    }

    public void setPrepAmpouleTime(Double prepAmpouleTime) {
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

    public Double getMedicineTime() {
        return medicineTime;
    }

    public void setMedicineTime(Double medicineTime) {
        this.medicineTime = medicineTime;
    }
}
