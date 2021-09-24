package org.imc.aml.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
public class SystemUnitClassLib {
    private String version;
    private Map<String, String> attributes;
    private Map<String, SystemUnitClass> systemUnitClassMap;
}
