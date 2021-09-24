package org.imc.aml.model.mapping;

import lombok.Data;
import org.imc.aml.model.enums.ReferenceEnum;
import org.imc.aml.model.enums.RelationEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AML关系映射到引用类型的
 */
public class ReferenceMapping {

    private static Map<String,String> map;
    ReferenceMapping(){
        map.put(RelationEnum.EXTERNALINTERFACE.getName(), ReferenceEnum.HASCOMPONENT.getName());
    }

    public static Map<String, String> getMap() {
        return map;
    }
}
