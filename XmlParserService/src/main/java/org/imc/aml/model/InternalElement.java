package org.imc.aml.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class InternalElement {
    private Map<String, String> attributes;
    private Map<String,Map<String,String>> newAttributeMap;
    private Map<String, String> externalInterfaceAttributes;
    private Map<String, String> supportedRoleClassAttributes;
    private Map<String, String> roleRequirementsAttributes;

    public InternalElement() {
        this.newAttributeMap = new HashMap<>();
        this.attributes = new HashMap<>();
        this.externalInterfaceAttributes = new HashMap<>();
        this.supportedRoleClassAttributes = new HashMap<>();
        this.roleRequirementsAttributes = new HashMap<>();
    }
}
