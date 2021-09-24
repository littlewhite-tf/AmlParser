package org.imc.aml.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class SystemUnitClass {
    private Map<String, String> attributesMap;
    private Map<String, String> externalInterfaceAttributes;
    private Map<String, String> supportedRoleClassAttributes;

    public SystemUnitClass() {
        this.attributesMap = new HashMap<>();
        this.externalInterfaceAttributes = new HashMap<>();
        this.supportedRoleClassAttributes = new HashMap<>();
    }
}
