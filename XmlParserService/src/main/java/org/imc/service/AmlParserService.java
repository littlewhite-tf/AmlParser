package org.imc.service;

import org.apache.maven.surefire.shade.org.apache.commons.lang3.tuple.Pair;
import org.imc.aml.model.InterfaceClassLib;
import org.imc.aml.model.RoleClassLib;
import org.imc.aml.model.SystemUnitClass;
import org.imc.aml.model.SystemUnitClassLib;
import org.imc.aml.model.enums.ReferenceEnum;
import org.imc.aml.model.enums.RelationEnum;
import org.imc.aml.model.enums.UADataTypeEnum;
import org.imc.aml.model.mapping.ReferenceMapping;
import org.imc.tools.DataValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Component
public class AmlParserService {

    private static Map<String, Node> basicObjectTypeMap = new HashMap<>();
    private static final String ALIAS  = "Alias";
    private static final String ROLECLASSLIB  = "RoleClassLib";
    private static final String INTERFACECLASSLIB  = "InterfaceClassLib";
    private static final String SYSTEMUNITCLASSLIB  = "SystemUnitClassLib";
    private static final String ROLECLASS = "RoleClass";
    private static final String NAME = "Name";
    private static final String ID = "ID";
    private static final String REFBASECLASSPATH = "RefBaseClassPath";
    private static final String VERSION = "Version";
    private static final String UAOBJECTTYPE = "UAObjectType";
    private static final String UAOBJECT = "UAObject";
    private static final String UAVARIABLE = "UAVariable";
    private static final String BROWSENAME = "BrowseName";
    private static final String PARENTNODEID = "ParentNodeId";
    private static final String DATATYPE = "DataType";
    private static final String REFERENCES="References";
    private static final String REFERENCE="Reference";
    private static final String VALUE="Value";
    private static final String REFERENCETYPE= "ReferenceType";
    private static final String NODEID = "NodeId";
    private static final String DISPLAYNAME = "DisplayName";
    private static final String UANODESET = "UANodeSet";
    private static final String INTERFACECLASS = "InterfaceClass";
    private static final String SYSTEMUNITCLASS = "SystemUnitClass";
    private static final String EXTERNALINTERFACE = "ExternalInterface ";
    private static final String SUPPORTEDROLECLASS = "SupportedRoleClass ";
    private static final String REFROLECLASSPATH="RefRoleClassPath";
    private static int i;
    private static int ns = 2;
    private Element opcUaXml;
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;
    private Document dom;


    private Map<String,RoleClassLib> roleClassLibMap = new HashMap<>();

    private Map<String, InterfaceClassLib> interfaceClassLibMap= new HashMap<>();

    private Map<String, SystemUnitClassLib> systemUnitClassLibMap= new HashMap<>();

    public void parseAml(String path) {
        parse(".\\XmlParserService\\src\\main\\resources\\Topology.aml");
        return;
    }

    private void parse(String path) {
        DocumentBuilderFactory readerFactory = DocumentBuilderFactory.newInstance();
        try {
            initOpcUaXml();
            Document d = builder.parse(path);

            //1.解析角色类库
            NodeList roleClassLibList = d.getElementsByTagName(ROLECLASSLIB);
            for(int j = 0;j<roleClassLibList.getLength();j++){
                parseRoleClassLib(roleClassLibList.item(j));
            }
            for(int j = 0;j<roleClassLibList.getLength();j++){
                parseRoleClassLib(roleClassLibList.item(j));
            }
            for(Map.Entry<String,RoleClassLib> entry:roleClassLibMap.entrySet()) {
                String roleClassLibName = entry.getKey();
                RoleClassLib roleClassLib = entry.getValue();
                buildRole(roleClassLib,roleClassLibName);
            }


//            //2.解析接口类库
//            NodeList interfaceClassLibList = d.getElementsByTagName(INTERFACECLASSLIB);
//            for(int j = 0;j<interfaceClassLibList.getLength();j++){
//                parseInterfaceClassLib(interfaceClassLibList.item(j));
//            }
//            //3.解析系统单元类库
//            NodeList systemUnitClassLibList = d.getElementsByTagName(SYSTEMUNITCLASSLIB);
//            for(int j = 0;j<systemUnitClassLibList.getLength();j++){
//                parseSystemUnitClassLib(systemUnitClassLibList.item(j));
//            }
//
//            //4.解析实例层次
//            Node instanceHierarchy = basicObjectTypeMap.get(CAEXFileBasicObjectTypeEnum.INSTANCEHIERARCHY.getName());


            // 写入文件
            //将现有结构转换为xml文件
            //创建TransformerFactory对象
            TransformerFactory tff = TransformerFactory.newInstance();
            //创建Transformer对象
            Transformer tf = tff.newTransformer();
            //设置换行
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            //调用Transformer对象的transform方法，创建xml文件
            tf.transform(new DOMSource(opcUaXml), new StreamResult(new File(".\\XmlParserService\\src\\main\\resources\\Topology.xml")));
          } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void parseRoleClassLib(Node roleClassLibNode) {
        NamedNodeMap attributes = roleClassLibNode.getAttributes();
        RoleClassLib roleClassLib = new RoleClassLib();
        // 1.属性
        for (int j = 0; j < attributes.getLength(); j++) {
            String key = attributes.item(j).getNodeName();
            String value  = attributes.item(j).getNodeValue();
            roleClassLib.getAttributes().put(key,value);
        }
        // 2.版本和RoleClass
        for (int j = 0; j < roleClassLibNode.getChildNodes().getLength(); j++) {
            Node roleClassNode = roleClassLibNode.getChildNodes().item(j);
            if (roleClassNode.getNodeType() == Node.ELEMENT_NODE && ROLECLASS.equals(roleClassNode.getNodeName())) {
                Map<String,String> attributeMap = new HashMap<>();
                NamedNodeMap roleClassNodeAttributes = roleClassNode.getAttributes();
                for (int i = 0; i < roleClassNodeAttributes.getLength(); i++) {
                    attributeMap.put(roleClassNodeAttributes.item(i).getNodeName(),roleClassNodeAttributes.item(i).getNodeValue());
                }
                roleClassLib.getRoleClassAttributes().put(attributeMap.get(NAME),attributeMap);

            }else if(roleClassNode.getNodeType() == Node.ELEMENT_NODE && VERSION.equals(roleClassNode.getNodeName())){
                roleClassLib.setVersion(roleClassNode.getFirstChild().getNodeValue());
            }
        }
        roleClassLibMap.put(roleClassLib.getAttributes().get(NAME),roleClassLib);
    }


    private void buildRole(RoleClassLib roleClassLib,String roleClassLibName) {
            for(Map.Entry<String,Map<String,String>> entry:roleClassLib.getRoleClassAttributes().entrySet()){
                String roleClassName = entry.getKey();
                Map<String,String> roleClassAttributes = entry.getValue();
                Element element = buildObjectTypeXml(roleClassName,roleClassAttributes);
                opcUaXml.appendChild(element);
            }
    }

    private void parseInterfaceClassLib(Node interfaceClassLibNode) {
        NamedNodeMap attributes = interfaceClassLibNode.getAttributes();
        InterfaceClassLib interfaceClassLib = new InterfaceClassLib();
        // 1.属性
        for (int i = 0; i < attributes.getLength(); i++) {
            interfaceClassLib.getAttributes().put(attributes.item(i).getNodeName(),attributes.item(i).getNodeValue());
        }
        // 2.版本和RoleClass
        for (int j = 0; j < interfaceClassLibNode.getChildNodes().getLength(); j++) {
            Node interfaceClassNode = interfaceClassLibNode.getChildNodes().item(j);
            if (interfaceClassNode.getNodeType() == Node.ELEMENT_NODE && INTERFACECLASS.equals(interfaceClassNode.getNodeName())) {
                buildInterface(interfaceClassNode,interfaceClassLib);
            }else if(interfaceClassNode.getNodeType() == Node.ELEMENT_NODE && VERSION.equals(interfaceClassNode.getNodeName())){
                interfaceClassLib.setVersion(interfaceClassNode.getNodeValue());
            }
        }
        interfaceClassLibMap.put(interfaceClassLib.getAttributes().get(NAME),interfaceClassLib);
    }

    private void buildInterface(Node interfaceNode,InterfaceClassLib interfaceClassLib) {
        NamedNodeMap attributes = interfaceNode.getAttributes();
        Map<String,String> attributeMap = new HashMap<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            attributeMap.put(attributes.item(i).getNodeName(),attributes.item(i).getNodeValue());
        }
        Element element = buildObjectTypeXml(attributeMap.get(NAME),attributeMap);
        interfaceClassLib.getInterfaceClassAttributes().put(attributeMap.get(NAME),attributeMap);
        opcUaXml.appendChild(element);
    }

    private void parseSystemUnitClassLib(Node systemUnitClassLibNode) {
        NamedNodeMap attributes = systemUnitClassLibNode.getAttributes();
        SystemUnitClassLib systemUnitClassLib = new SystemUnitClassLib();
        // 1.属性
        for (int i = 0; i < attributes.getLength(); i++) {
            systemUnitClassLib.getAttributes().put(attributes.item(i).getNodeName(),attributes.item(i).getNodeValue());
        }
        // 2.版本和RoleClass
        for (int j = 0; j < systemUnitClassLibNode.getChildNodes().getLength(); j++) {
            Node sonOfLib = systemUnitClassLibNode.getChildNodes().item(j);
            if (sonOfLib.getNodeType() == Node.ELEMENT_NODE && SYSTEMUNITCLASS.equals(sonOfLib.getNodeName())) {
                // 解析单个系统单元类，如电动螺丝刀类
                Pair<Element,SystemUnitClass> result = buildSystemUnitClass(sonOfLib);
                opcUaXml.appendChild(result.getKey());
                SystemUnitClass systemUnitClass = result.getValue();
                systemUnitClassLib.getSystemUnitClassMap().put(systemUnitClass.getAttributesMap().get(NAME),systemUnitClass);
            }else if(sonOfLib.getNodeType() == Node.ELEMENT_NODE && VERSION.equals(sonOfLib.getNodeName())){
                systemUnitClassLib.setVersion(sonOfLib.getNodeValue());
            }
        }
        systemUnitClassLibMap.put(systemUnitClassLib.getAttributes().get(NAME),systemUnitClassLib);
    }

    private void initOpcUaXml() throws ParserConfigurationException {
        i = 1;
        builder = factory.newDocumentBuilder();
        dom = builder.newDocument();
        opcUaXml = dom.createElement(UANODESET);
        // 1.属性
        opcUaXml.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        opcUaXml.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        opcUaXml.setAttribute("xmlns", "http://opcfoundation.org/UA/2011/03/UANodeSet.xsd");

        // 2.命名空间
        Element namespaceUris = dom.createElement("NamespaceUris");
        Element uri1 =  dom.createElement("Uri");
        Element uri2 =  dom.createElement("Uri");
        uri1.setTextContent("http://opcfoundation.org/UA/AML/");
        uri2.setTextContent("http://www.iosb.fraunhofer.de/Topology.aml");
        namespaceUris.appendChild(uri1);
        namespaceUris.appendChild(uri2);
        opcUaXml.appendChild(namespaceUris);

        // 3.Alias
        appendAliases();
    }

    private void appendAliases(){
        Element aliases = dom.createElement("Aliases");

        Map<String,String> mp = new HashMap<>();
        mp.put(ALIAS,"Boolean");
        Element alias = buildBeanAttributeValue(ALIAS,"i=1",mp);
        aliases.appendChild(alias);
        mp.put(ALIAS,"SByte");
        alias = buildBeanAttributeValue(ALIAS,"i=2",mp);
        aliases.appendChild(alias);
        //todo 把其它aliases加进来

        opcUaXml.appendChild(aliases);
    }


    private Element buildObjectTypeXml(String name,Map<String,String> attributeMap) {

        Element element = dom.createElement(UAOBJECTTYPE);
        // 设置属性
        element.setAttribute(BROWSENAME, name);
        String nodeId = generateNodeId();
        attributeMap.put(NODEID,nodeId);

        element.setAttribute(NODEID, nodeId);
        Element displayNameBean = buildBeanValue(DISPLAYNAME, name);
        element.appendChild(displayNameBean);
        return element;
    }

    private Pair<Element,SystemUnitClass> buildSystemUnitClass(Node systemUnitClassNode) {
        NamedNodeMap attributes = systemUnitClassNode.getAttributes();
        SystemUnitClass systemUnitClass = new SystemUnitClass();
        // 1.构造工程类相关
        // 1.1.属性
        for (int i = 0; i < attributes.getLength(); i++) {
            systemUnitClass.getAttributesMap().put(attributes.item(i).getNodeName(),attributes.item(i).getNodeValue());
        }
        String name = systemUnitClass.getAttributesMap().get(NAME);
        // 1.2.SupportedRoleClass和ExternalInterface
        for (int j = 0; j < systemUnitClassNode.getChildNodes().getLength(); j++) {
            Node sonOfSystemUnitClass = systemUnitClassNode.getChildNodes().item(j);
            if (EXTERNALINTERFACE.equals(sonOfSystemUnitClass.getNodeName())) {
                for (int i = 0; i < sonOfSystemUnitClass.getAttributes().getLength(); i++) {
                    systemUnitClass.getExternalInterfaceAttributes().put(
                            sonOfSystemUnitClass.getAttributes().item(i).getNodeName()
                            ,sonOfSystemUnitClass.getAttributes().item(i).getNodeValue());
                }
            }else if(SUPPORTEDROLECLASS.equals(sonOfSystemUnitClass.getNodeName())){
                for (int i = 0; i < sonOfSystemUnitClass.getAttributes().getLength(); i++) {
                    systemUnitClass.getSupportedRoleClassAttributes().put(
                            sonOfSystemUnitClass.getAttributes().item(i).getNodeName()
                            ,sonOfSystemUnitClass.getAttributes().item(i).getNodeValue());
                }
            }
        }
        // 2. 写Element相关
        Element element = dom.createElement(UAOBJECTTYPE);
        // 设置属性
        element.setAttribute(BROWSENAME, name);
        String nodeId = generateNodeId();
        element.setAttribute(NODEID, nodeId);
        // 一级子节点 displayName
        Element displayNameBean = buildBeanValue(DISPLAYNAME, name);
        element.appendChild(displayNameBean);
        // 一级子节点References
        Element refersEle = dom.createElement(REFERENCES);
        // ExternalInterface
        if(systemUnitClass.getExternalInterfaceAttributes().size() > 0 ){
            String objectName = systemUnitClass.getExternalInterfaceAttributes().get(NAME);
            String refBaseClassPath = systemUnitClass.getExternalInterfaceAttributes().get(REFBASECLASSPATH);
            String[] refPath = refBaseClassPath.split("/");
            String interfaceClassLibName = refPath[0];
            String interfaceClassName = refPath[1];
            String refNodeId = interfaceClassLibMap.get(interfaceClassLibName).getInterfaceClassAttributes().get(interfaceClassName).get(NODEID);
            // 根据接口创建EnergySupply对象节点，及其引用
            String objectNodeId = buildObjectByInterface(refNodeId,element.getAttribute(NODEID),objectName,systemUnitClass.getExternalInterfaceAttributes());
            //创建ElectricScrewdriver的引用
            Element ref = dom.createElement(REFERENCE);
            ref.setAttribute(REFERENCETYPE, ReferenceMapping.getMap().get(RelationEnum.EXTERNALINTERFACE.getName()));
            ref.setTextContent(objectNodeId);
            refersEle.appendChild(ref);
        }

        // SupportedRoleClass
        if(systemUnitClass.getSupportedRoleClassAttributes().size() > 0 ){
            String refRoleClassPath = systemUnitClass.getSupportedRoleClassAttributes().get(REFROLECLASSPATH);
            String[] refPath = refRoleClassPath.split("/");
            String roleClassLibName = refPath[0];
            String roleClassName = refPath[1];
            String refNodeId = roleClassLibMap.get(roleClassLibName).getRoleClassAttributes().get(roleClassName).get(NODEID);
            //创建ElectricScrewdriver的引用
            Element ref = dom.createElement(REFERENCE);
            ref.setAttribute(REFERENCETYPE, ReferenceMapping.getMap().get(RelationEnum.SUPPORTEDROLECLASS.getNodeId()));
            ref.setTextContent(refNodeId);
            refersEle.appendChild(ref);
        }
        element.appendChild(refersEle);
        return Pair.of(element,systemUnitClass);
    }

    private String buildObjectByInterface(String refNodeId,String parentNodeId,String objectName,Map<String,String> additionalVariable){
        String nodeId = generateNodeId();
        Element object = dom.createElement(UAOBJECT);
        object.setAttribute(NODEID, nodeId);
        object.setAttribute(BROWSENAME,objectName);
        object.setAttribute(PARENTNODEID, parentNodeId);

        Element displayName = buildBeanValue(DISPLAYNAME,objectName);
        object.appendChild(displayName);

        Element refersEle = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        ref.setTextContent(refNodeId);
        refersEle.appendChild(ref);

        for (Map.Entry<String, String> entry : additionalVariable.entrySet()) {
            if(!entry.getKey().equals(NAME)&&!entry.getKey().equals(REFBASECLASSPATH)){
                String variableNodeId = buildPropertyNode(entry.getKey(),entry.getValue(),nodeId);
                Element refVariable = dom.createElement(REFERENCE);
                refVariable.setAttribute(REFERENCETYPE, ReferenceEnum.HASPROPERTY.getName());
                refVariable.setTextContent(variableNodeId);
                refersEle.appendChild(refVariable);
            }
        }

        ref.setTextContent(refNodeId);
        refersEle.appendChild(ref);

        object.appendChild(refersEle);
        opcUaXml.appendChild(object);
        return nodeId;
    }

    private String buildPropertyNode(String name, String value, String parentNodeId){
        String nodeId = generateNodeId();

        Element variable = dom.createElement(UAVARIABLE);
        variable.setAttribute(NODEID, nodeId);
        variable.setAttribute(BROWSENAME,name);
        variable.setAttribute(PARENTNODEID, parentNodeId);
        Element displayName = buildBeanValue(DISPLAYNAME,name);
        variable.appendChild(displayName);

        if(DataValidate.isInteger(value)){
            variable.setAttribute(DATATYPE, UADataTypeEnum.INT.getName());
        }else if(DataValidate.isDouble(value)){
            variable.setAttribute(DATATYPE, UADataTypeEnum.DOUBLE.getName());
        }else{
            variable.setAttribute(DATATYPE, UADataTypeEnum.STRING.getName());
        }
        HashMap<String, String> valueAttribute = new HashMap<String, String>();
        valueAttribute.put("xmlns", "http://opcfoundation.org/UA/2008/02/Types.xsd");
        Element valueBean = buildBeanAttributeValue(VALUE,value,valueAttribute);
        variable.appendChild(valueBean);

        Element refersEle = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE,ReferenceEnum.HASTYPEDEFINITION.getName());
        ref.setTextContent("i=68");
        refersEle.appendChild(ref);
        variable.appendChild(refersEle);
        opcUaXml.appendChild(variable);
        return nodeId;
    }

    private Element buildBeanValue(String bean, String value) {
        Element element = dom.createElement(bean);
        element.setTextContent(value);
        return element;

    }

    private Element buildBeanAttributeValue(String bean, String value,Map<String,String> map) {
        Element element = dom.createElement(bean);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            element.setAttribute(entry.getKey(),entry.getValue());
        }
        element.setTextContent(value);
        return element;
    }

    private String generateNodeId() {
        return String.format("ns=%s;i=%s", ns, i++);
    }
}
