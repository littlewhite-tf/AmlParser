package org.imc.aml.model.mapping;

import lombok.Data;
import org.imc.aml.model.enums.ReferenceEnum;
import org.imc.aml.model.enums.RelationEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * AML关系映射到引用类型的
 */
@Component
public class RelationReferenceMapping {

    private static Map<String,String> map;
    RelationReferenceMapping(){
        map = new HashMap<>();
        map.put(RelationEnum.EXTERNALINTERFACE.getName(), ReferenceEnum.HASCOMPONENT.getName());
        map.put(RelationEnum.SUPPORTEDROLECLASS.getName(), "ns=1;i=4001");
    }

    public static Map<String, String> getMap() {
        return map;
    }
}
