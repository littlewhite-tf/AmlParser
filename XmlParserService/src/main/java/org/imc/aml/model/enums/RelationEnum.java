package org.imc.aml.model.enums;

import org.springframework.stereotype.Component;

/**
 * AML关系类型
 */
public enum RelationEnum {

    /**
     * 外部接口关系
     */
    EXTERNALINTERFACE("ExternalInterface",null),

    /**
     * 支持角色类
     */
    SUPPORTEDROLECLASS("SupportedRoleClass","ns=1;i=4001")
    ;

    private String name;
    private String nodeId;

    RelationEnum(String name, String nodeId) {
        this.name = name;
        this.nodeId = nodeId;
    }

    RelationEnum() {
    }

    public String getName() {
        return name;
    }

    public String getNodeId() {
        return nodeId;
    }
}
