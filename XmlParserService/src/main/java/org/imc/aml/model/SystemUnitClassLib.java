package org.imc.aml.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class SystemUnitClassLib {
    private String version;
    private Map<String, String> attributes;
    private Map<String, SystemUnitClass> systemUnitClassMap;

    public SystemUnitClassLib() {
        this.version = new String();
        this.attributes = new HashMap<>();
        this.systemUnitClassMap = new HashMap<>();
    }
}
