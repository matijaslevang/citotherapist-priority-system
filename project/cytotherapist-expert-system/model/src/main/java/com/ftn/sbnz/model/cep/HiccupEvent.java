package com.ftn.sbnz.model.cep;

import org.kie.api.definition.type.Role;

import java.io.Serializable;

@Role(Role.Type.EVENT)
public class HiccupEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public HiccupEvent() {
        super();
    }
}
