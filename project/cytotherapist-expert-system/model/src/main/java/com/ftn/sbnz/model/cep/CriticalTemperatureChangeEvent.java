package com.ftn.sbnz.model.cep;

import org.kie.api.definition.type.Role;

import java.io.Serializable;

@Role(Role.Type.EVENT)
public class CriticalTemperatureChangeEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public CriticalTemperatureChangeEvent() {
        super();
    }
}
