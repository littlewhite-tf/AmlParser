package org.imc.aml.model.enums;

import org.springframework.stereotype.Component;

/**
 * AML基本对象类型
 */
public enum CAEXFileBasicObjectTypeEnum {

    /**
     * 实例层次
     */
    INSTANCEHIERARCHY("InstanceHierarchy"),

    /**
     * 接口类库
     */
    INTERFACECLASSLIB("InterfaceClassLib"),

    /**
     * 角色类库
     */
    ROLECLASSLIB("RoleClassLib"),

    /**
     * 系统单元类库
     */
    SYSTEMUNITCLASSLIB("SystemUnitClassLib");

    private String name;

    CAEXFileBasicObjectTypeEnum(String name) {
        this.name = name;
    }

    CAEXFileBasicObjectTypeEnum() {
    }

    public String getName() {
        return name;
    }

}
