package org.imc.aml.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class InterfaceClassLib {
    private String version;
    private Map<String, String> attributes;
    private Map<String, Map<String, String>> interfaceClassAttributes;

    public InterfaceClassLib() {
        this.version = new String();
        this.attributes = new HashMap<>();
        this.interfaceClassAttributes = new HashMap<>();
    }
}
