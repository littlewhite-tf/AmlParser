package org.imc.aml.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class InstanceHierarchy {
    private Map<String,InternalElement> internalElementMap;
    private Map<String, String> attributes;

    public InstanceHierarchy(){
        this.internalElementMap= new HashMap<>();
        this.attributes = new HashMap<>();

    }
}
