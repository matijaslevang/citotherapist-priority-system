package com.ftn.sbnz.model.cep;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import java.io.Serializable;

@Role(Role.Type.EVENT)
@Expires("5s")
public class PressureReadingEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private int upperPressure;
    private int lowerPressure;

    public PressureReadingEvent() {
        super();
    }

    public PressureReadingEvent(int upperPressure, int lowerPressure) {
        this.upperPressure = upperPressure;
        this.lowerPressure = lowerPressure;
    }

    public int getUpperPressure() {
        return upperPressure;
    }

    public void setUpperPressure(int upperPressure) {
        this.upperPressure = upperPressure;
    }

    public int getLowerPressure() {
        return lowerPressure;
    }

    public void setLowerPressure(int lowerPressure) {
        this.lowerPressure = lowerPressure;
    }
}