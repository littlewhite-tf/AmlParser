package org.imc.aml.model.enums;

import org.springframework.stereotype.Component;

/**
 * OPCUA引用类型枚举
 */
public enum ReferenceEnum {

    /**
     * 拥有组成成分
     */
    HASCOMPONENT("HasComponent"),

    /**
     * 接口实现
     */
    HASTYPEDEFINITION("HasTypeDefinition"),

    /**
     * 有属性
     */
    HASPROPERTY("HasProperty"),

    /**
     * 有属性
     */
    HASSUBTYPE("HasSubtype"),

    /**
     * 有属性
     */
    ORGANIZES("Organizes");

    private String name;

    ReferenceEnum(String name) {
        this.name = name;
    }

    ReferenceEnum() {
    }

    public String getName() {
        return name;
    }

}
