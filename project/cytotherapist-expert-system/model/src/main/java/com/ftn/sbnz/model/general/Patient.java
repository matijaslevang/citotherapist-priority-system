package com.ftn.sbnz.model.general;

public class Patient {

    private double currentTemperature;   // x
    private int currentUpperPressure;   // y
    private int currentLowerPressure;    // z
    private double currentBreathingCycle; // m

    public Patient() {}

    public Patient(double currentTemperature, int currentUpperPressure, int currentLowerPressure, double currentBreathingCycle) {
        this.currentTemperature = currentTemperature;
        this.currentUpperPressure = currentUpperPressure;
        this.currentLowerPressure = currentLowerPressure;
        this.currentBreathingCycle = currentBreathingCycle;
    }

    public double getCurrentTemperature() { return currentTemperature; }
    public void setCurrentTemperature(double currentTemperature) { this.currentTemperature = currentTemperature; }

    public int getCurrentUpperPressure() { return currentUpperPressure; }
    public void setCurrentUpperPressure(int currentUpperPressure) { this.currentUpperPressure = currentUpperPressure; }

    public int getCurrentLowerPressure() { return currentLowerPressure; }
    public void setCurrentLowerPressure(int currentLowerPressure) { this.currentLowerPressure = currentLowerPressure; }

    public double getCurrentBreathingCycle() { return currentBreathingCycle; }
    public void setCurrentBreathingCycle(double currentBreathingCycle) { this.currentBreathingCycle = currentBreathingCycle; }
}