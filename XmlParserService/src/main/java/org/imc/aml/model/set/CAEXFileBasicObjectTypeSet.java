package org.imc.aml.model.set;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * AML基本对象类型集合
 */
@Component
public class CAEXFileBasicObjectTypeSet {
    public static Set<String> enumSet = new HashSet<String>() {{
        add("InstanceHierarchy");
        add("InterfaceClassLib");
        add("RoleClassLib");
        add("SystemUnitClassLib");
    }};
}
