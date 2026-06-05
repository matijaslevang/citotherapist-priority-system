package com.ftn.sbnz.model.cep;

import java.io.Serializable;

import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
public class MinimumTemperatureChangeEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public MinimumTemperatureChangeEvent() {
        super();
    }
}
