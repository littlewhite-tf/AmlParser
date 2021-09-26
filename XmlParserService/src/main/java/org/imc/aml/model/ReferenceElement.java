package org.imc.aml.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class ReferenceElement {
    private String value;
    private Map<String, String> attributes;

    public ReferenceElement() {
        this.value = new String();
        this.attributes = new HashMap<>();
    }
}
