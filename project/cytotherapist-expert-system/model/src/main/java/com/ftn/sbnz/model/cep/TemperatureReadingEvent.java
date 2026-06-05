package com.ftn.sbnz.model.cep;

import org.kie.api.definition.type.Role;
import java.io.Serializable;

@Role(Role.Type.EVENT)
public class TemperatureReadingEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private double value;

    public TemperatureReadingEvent() {
        super();
    }

    public TemperatureReadingEvent(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}