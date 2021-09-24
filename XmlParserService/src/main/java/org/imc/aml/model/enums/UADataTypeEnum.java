package org.imc.aml.model.enums;

import org.springframework.stereotype.Component;

/**
 * AML关系类型
 */
public enum UADataTypeEnum {


    INT("Int"),

    DOUBLE("Double"),

    STRING("String")
    ;

    private String name;


    UADataTypeEnum(String name) {
        this.name = name;
    }

    UADataTypeEnum() {
    }

    public String getName() {
        return name;
    }
}
