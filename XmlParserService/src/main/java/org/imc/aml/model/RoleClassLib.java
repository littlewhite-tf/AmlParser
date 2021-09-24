package org.imc.aml.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class RoleClassLib {
    private String version;
    private Map<String, String> attributes;
    private Map<String, Map<String, String>> roleClassAttributes;


    public RoleClassLib() {
        attributes = new HashMap<>();
        roleClassAttributes = new HashMap<>();
        version = new String();
    }
}
