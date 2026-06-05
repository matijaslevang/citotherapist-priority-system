package com.ftn.sbnz.model.general;

public class Patient {

    private double currentTemperature;   // x
    private int currentUpperPressure;   // y
    private int currentLowerPressure;    // z
    private double currentBreathingCycle; // m
    private String protocol;
    private int currentCycle;

    public Patient() {}

    public Patient(double currentTemperature, int currentUpperPressure, int currentLowerPressure, double currentBreathingCycle,
        String protocol, int currentCycle
    ) {
        this.currentTemperature = currentTemperature;
        this.currentUpperPressure = currentUpperPressure;
        this.currentLowerPressure = currentLowerPressure;
        this.currentBreathingCycle = currentBreathingCycle;
        this.protocol = protocol;
        this.currentCycle = currentCycle;
    }

    public double getCurrentTemperature() { return currentTemperature; }
    public void setCurrentTemperature(double currentTemperature) { this.currentTemperature = currentTemperature; }

    public int getCurrentUpperPressure() { return currentUpperPressure; }
    public void setCurrentUpperPressure(int currentUpperPressure) { this.currentUpperPressure = currentUpperPressure; }

    public int getCurrentLowerPressure() { return currentLowerPressure; }
    public void setCurrentLowerPressure(int currentLowerPressure) { this.currentLowerPressure = currentLowerPressure; }

    public double getCurrentBreathingCycle() { return currentBreathingCycle; }
    public void setCurrentBreathingCycle(double currentBreathingCycle) { this.currentBreathingCycle = currentBreathingCycle; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public int getCurrentCycle() { return currentCycle; }
    public void setCurrentCycle(int currentCycle) { this.currentCycle = currentCycle; }
}