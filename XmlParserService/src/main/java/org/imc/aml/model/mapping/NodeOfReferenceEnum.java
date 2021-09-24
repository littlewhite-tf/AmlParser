package org.imc.aml.model.mapping;

import org.springframework.stereotype.Component;

/**
 * AML需要映射为引用类型的关系类型
 */
public enum NodeOfReferenceEnum {

    SUPPORTEDROLECLASS("SupportedRoleClass","ns=1;i=4001");

    private String name;

    private String nodeId;

    NodeOfReferenceEnum(String name, String nodeId) {
        this.name = name;
        this.nodeId = nodeId;
    }

    NodeOfReferenceEnum() {
    }

    public String getName() {
        return name;
    }

    public String getNodeId(){
        return nodeId;
    }
}
