package org.imc.aml.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
public class InterfaceClassLib {
    private String version;
    private Map<String, String> attributes;
    private Map<String, Map<String, String>> interfaceClassAttributes;
}
