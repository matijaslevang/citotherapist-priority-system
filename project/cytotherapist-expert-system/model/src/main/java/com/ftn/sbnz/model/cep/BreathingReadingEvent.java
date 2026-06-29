package com.ftn.sbnz.model.cep;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import java.io.Serializable;

@Role(Role.Type.EVENT)
@Expires("5s")
public class BreathingReadingEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private double cycleLength;

    public BreathingReadingEvent() {
        super();
    }

    public BreathingReadingEvent(double cycleLength) {
        this.cycleLength = cycleLength;
    }

    public double getCycleLength() {
        return cycleLength;
    }

    public void setCycleLength(double cycleLength) {
        this.cycleLength = cycleLength;
    }
}