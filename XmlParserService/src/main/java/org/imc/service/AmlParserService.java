package org.imc.service;

import org.imc.aml.model.*;
import org.imc.aml.model.enums.ReferenceEnum;
import org.imc.aml.model.enums.RelationEnum;
import org.imc.aml.model.enums.UADataTypeEnum;
import org.imc.aml.model.mapping.RelationReferenceMapping;
import org.imc.tools.DataValidate;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class AmlParserService {

    private static Map<String, Node> basicObjectTypeMap = new HashMap<>();
    private static final String ALIAS = "Alias";
    private static final String ROLECLASSLIB = "RoleClassLib";
    private static final String INTERFACECLASSLIB = "InterfaceClassLib";
    private static final String INSTANCEHIERARCHY = "InstanceHierarchy";
    private static final String INTERNALELEMENT = "InternalElement";
    private static final String SYSTEMUNITCLASSLIB = "SystemUnitClassLib";
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
    private static final String REFERENCES = "References";
    private static final String REFERENCE = "Reference";
    private static final String VALUE = "Value";
    private static final String REFERENCETYPE = "ReferenceType";
    private static final String ISFORWARD = "IsForward";
    private static final String NODEID = "NodeId";
    private static final String DISPLAYNAME = "DisplayName";
    private static final String UANODESET = "UANodeSet";
    private static final String INTERFACECLASS = "InterfaceClass";
    private static final String SYSTEMUNITCLASS = "SystemUnitClass";
    private static final String EXTERNALINTERFACE = "ExternalInterface";
    private static final String SUPPORTEDROLECLASS = "SupportedRoleClass";
    private static final String ATTRIBUTE = "Attribute";
    private static final String REFROLECLASSPATH = "RefRoleClassPath";
    private static final String REFBASESYSTEMUNITPATH = "RefBaseSystemUnitPath";
    private static final String ROLEREQUIREMENTS = "RoleRequirements";
    private static final String FALSE = "false";


    private static int i;
    private static int ns = 2;
    private Element opcUaXml;
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;
    private Document dom;


    private Map<String, RoleClassLib> roleClassLibMap = new HashMap<>();

    private Map<String, InterfaceClassLib> interfaceClassLibMap = new HashMap<>();

    private Map<String, SystemUnitClassLib> systemUnitClassLibMap = new HashMap<>();

    private Map<String, InstanceHierarchy> instanceHierarchyMap = new HashMap<>();

    private Map<String, Element> instanceDirectoryMap = new HashMap<>();

    private Element instanceHierarchiesDirectory;
    private Element interfaceClassLibsDirectory;
    private Element systemUnitClassLibsDirectory;
    private Element roleClassLibsDirectory;

    private Element topologyElement;

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
            // 1.1初始化内部数据结构
            for (int j = 0; j < roleClassLibList.getLength(); j++) {
                parseRoleClassLib(roleClassLibList.item(j));
            }
            // 1.2构造输出Element
            for (Map.Entry<String, RoleClassLib> entry : roleClassLibMap.entrySet()) {
                String roleClassLibName = entry.getKey();
                RoleClassLib roleClassLib = entry.getValue();
                buildRole(roleClassLib, roleClassLibName);
            }


            //2.解析接口类库
            NodeList interfaceClassLibList = d.getElementsByTagName(INTERFACECLASSLIB);
            // 2.1初始化内部数据结构
            for (int j = 0; j < interfaceClassLibList.getLength(); j++) {
                parseInterfaceClassLib(interfaceClassLibList.item(j));
            }
            // 2.2构造输出Element
            for (Map.Entry<String, InterfaceClassLib> entry : interfaceClassLibMap.entrySet()) {
                String interfaceClassLibName = entry.getKey();
                InterfaceClassLib interfaceClassLib = entry.getValue();
                buildInterface(interfaceClassLib, interfaceClassLibName);
            }

            //3.解析系统单元类库
            NodeList systemUnitClassLibList = d.getElementsByTagName(SYSTEMUNITCLASSLIB);
            // 3.1初始化内部数据结构
            for (int j = 0; j < systemUnitClassLibList.getLength(); j++) {
                parseSystemUnitClassLib(systemUnitClassLibList.item(j));
            }
            // 3.2构造输出Element
            for (Map.Entry<String, SystemUnitClassLib> entry : systemUnitClassLibMap.entrySet()) {
                String systemUnitClassLibName = entry.getKey();
                SystemUnitClassLib systemUnitClassLib = entry.getValue();
                Element systemUnitClassLibElement = dom.createElement(UAOBJECT);
                String systemUnitClassNodeId = generateNodeId();
                systemUnitClassLib.getAttributes().put(NODEID,systemUnitClassNodeId);
                systemUnitClassLibElement.setAttribute(NODEID, systemUnitClassNodeId);
                systemUnitClassLibElement.setAttribute(BROWSENAME, systemUnitClassLibName);
                Element display = buildBeanValue(DISPLAYNAME, systemUnitClassLibName);
                systemUnitClassLibElement.appendChild(display);
                Element refs = dom.createElement(REFERENCES);
                Element ref = dom.createElement(REFERENCE);
                ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
                ref.setTextContent("i=61");
                refs.appendChild(ref);
                Element ref1 = dom.createElement(REFERENCE);
                ref1.setAttribute(REFERENCETYPE, ReferenceEnum.ORGANIZES.getName());
                ref1.setAttribute(ISFORWARD, FALSE);
                ref1.setTextContent("ns=1;i=5010");
                refs.appendChild(ref1);
                Map<String, String> addRefMap = new HashMap<>();
                addRefMap.put(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
                String version = systemUnitClassLib.getVersion();
                systemUnitClassLibElement.appendChild(refs);
                opcUaXml.appendChild(systemUnitClassLibElement);

                buildPropertyNode(VERSION, version, systemUnitClassNodeId,systemUnitClassLibElement);
                addReference(systemUnitClassLibsDirectory, systemUnitClassNodeId, addRefMap);

                for (Map.Entry<String, SystemUnitClass> systemUnitClassEntry : systemUnitClassLib.getSystemUnitClassMap().entrySet()) {
                    String systemUnitClassName = systemUnitClassEntry.getKey();
                    SystemUnitClass systemUnitClass = systemUnitClassEntry.getValue();
                    Element systemUnitClassElement = buildSystemUnitClassElement(systemUnitClass, systemUnitClassLibElement);
                    opcUaXml.appendChild(systemUnitClassElement);
                }
            }

            //4.解析实例层次
            NodeList instanceHierarchyList = d.getElementsByTagName(INSTANCEHIERARCHY);
            // 4.1初始化内部数据结构,且构造文件夹Element
            for (int j = 0; j < instanceHierarchyList.getLength(); j++) {
                parseInstanceHierarchy(instanceHierarchyList.item(j));
            }
            // 3.2构造输出Element
            for (Map.Entry<String, InstanceHierarchy> entry : instanceHierarchyMap.entrySet()) {
                String instanceHierarchyName = entry.getKey();
                InstanceHierarchy instanceHierarchy = entry.getValue();
                Element instanceDirectory = instanceDirectoryMap.get(instanceHierarchyName);
                addReferenceToParent(instanceHierarchiesDirectory,instanceDirectory,ReferenceEnum.HASCOMPONENT.getName());
                // 遍历每个Element
                for (Map.Entry<String, InternalElement> instanceElementEntry : instanceHierarchy.getInternalElementMap().entrySet()) {
                    InternalElement internalElement = instanceElementEntry.getValue();
                    Element internalXmlElement = buildInternalXmlElement(internalElement, instanceDirectory);
                    opcUaXml.appendChild(internalXmlElement);
                }
            }

            // 写入文件
            exportElementToFile(opcUaXml, "Topology.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element buildInternalXmlElement(InternalElement internalElement, Element instanceDirectory) {
        String nodeId = generateNodeId();
        Node directoryRefs = dom.createElement(REFERENCES);
        for (int j = 0; j < instanceDirectory.getChildNodes().getLength(); j++) {
            if (REFERENCES.equals(instanceDirectory.getChildNodes().item(j).getNodeName())) {
                directoryRefs = instanceDirectory.getChildNodes().item(j);
            }
        }
        Element directoryRef = dom.createElement(REFERENCE);
        directoryRef.setAttribute(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        directoryRef.setTextContent(nodeId);
        directoryRefs.appendChild(directoryRef);
        instanceDirectory.appendChild(directoryRefs);


        String name = internalElement.getAttributes().get(NAME);

        // 2. 写Element相关
        Element element = dom.createElement(UAOBJECT);
        // 设置属性
        element.setAttribute(BROWSENAME, name);
        element.setAttribute(NODEID, nodeId);
        // 一级子节点 displayName
        Element displayNameBean = buildBeanValue(DISPLAYNAME, name);
        element.appendChild(displayNameBean);
        // 一级子节点References
        Element refersEle = dom.createElement(REFERENCES);

        element.appendChild(refersEle);
        // ExternalInterface
        if (internalElement.getExternalInterfaceAttributes().size() > 0) {
            String objectName = internalElement.getExternalInterfaceAttributes().get(NAME);
            String refBaseClassPath = internalElement.getExternalInterfaceAttributes().get(REFBASECLASSPATH);
            String[] refPath = refBaseClassPath.split("/");
            String interfaceClassLibName = refPath[0];
            String interfaceClassName = refPath[1];
            String refNodeId = interfaceClassLibMap.get(interfaceClassLibName).getInterfaceClassAttributes().get(interfaceClassName).get(NODEID);
            // 根据接口创建EnergySupply对象节点，及其引用
            String objectNodeId = buildObjectByInterface(refNodeId, element.getAttribute(NODEID), objectName, internalElement.getExternalInterfaceAttributes());
            //创建ElectricScrewdriver的引用
            Element ref = dom.createElement(REFERENCE);
            ref.setAttribute(REFERENCETYPE, RelationReferenceMapping.getMap().get(RelationEnum.EXTERNALINTERFACE.getName()));
            ref.setTextContent(objectNodeId);
            refersEle.appendChild(ref);
        }

        // SupportedRoleClass
        if (internalElement.getSupportedRoleClassAttributes().size() > 0) {
            String refRoleClassPath = internalElement.getSupportedRoleClassAttributes().get(REFROLECLASSPATH);
            String[] refPath = refRoleClassPath.split("/");
            String roleClassLibName = refPath[0];
            String roleClassName = refPath[1];
            String refNodeId = roleClassLibMap.get(roleClassLibName).getRoleClassAttributes().get(roleClassName).get(NODEID);
            //创建ElectricScrewdriver的引用
            Element ref = dom.createElement(REFERENCE);
            ref.setAttribute(REFERENCETYPE, RelationReferenceMapping.getMap().get(RelationEnum.SUPPORTEDROLECLASS.getName()));
            ref.setTextContent(refNodeId);
            refersEle.appendChild(ref);
        }

        // newAttribute
        for (Map.Entry<String, Map<String, String>> entry : internalElement.getNewAttributeMap().entrySet()) {
            String newAttributeName = entry.getKey();
            Map<String, String> newAttributeMessageMap = entry.getValue();
            String refNodeId = buildNewVariableNode(newAttributeMessageMap.get(NAME), newAttributeMessageMap.get(VALUE) != null ? newAttributeMessageMap.get(VALUE) : "", nodeId);
            Element ref = dom.createElement(REFERENCE);
            ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
            ref.setTextContent(refNodeId);
            refersEle.appendChild(ref);
        }


        // attributes
        for (Map.Entry<String, String> entry : internalElement.getAttributes().entrySet()) {
            String key = entry.getKey();
            if (!key.equals(NAME) && !key.equals(REFBASECLASSPATH) && !key.equals(REFBASESYSTEMUNITPATH)) {
                buildPropertyNode(entry.getKey(), entry.getValue(), nodeId,element);
            } else if (key.equals(REFBASESYSTEMUNITPATH)) {
                String refBaseSystemUnitPath = entry.getValue();
                String[] refPath = refBaseSystemUnitPath.split("/");
                String systemUnitClassLibName = refPath[0];
                String systemUnitClassName = refPath[1];
                String refNodeId = systemUnitClassLibMap.get(systemUnitClassLibName).getSystemUnitClassMap().get(systemUnitClassName).getAttributesMap().get(NODEID);
                Element ref = dom.createElement(REFERENCE);
                ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
                ref.setTextContent(refNodeId);
                refersEle.appendChild(ref);
            }
        }

        // roleRequirements暂不解析

        return element;
    }

    private void parseRoleClassLib(Node roleClassLibNode) {
        NamedNodeMap attributes = roleClassLibNode.getAttributes();
        RoleClassLib roleClassLib = new RoleClassLib();
        // 1.属性
        for (int j = 0; j < attributes.getLength(); j++) {
            String key = attributes.item(j).getNodeName();
            String value = attributes.item(j).getNodeValue();
            roleClassLib.getAttributes().put(key, value);
        }
        // 2.版本和RoleClass
        for (int j = 0; j < roleClassLibNode.getChildNodes().getLength(); j++) {
            Node roleClassNode = roleClassLibNode.getChildNodes().item(j);
            if (roleClassNode.getNodeType() == Node.ELEMENT_NODE && ROLECLASS.equals(roleClassNode.getNodeName())) {
                Map<String, String> attributeMap = new HashMap<>();
                NamedNodeMap roleClassNodeAttributes = roleClassNode.getAttributes();
                for (int i = 0; i < roleClassNodeAttributes.getLength(); i++) {
                    attributeMap.put(roleClassNodeAttributes.item(i).getNodeName(), roleClassNodeAttributes.item(i).getNodeValue());
                }
                roleClassLib.getRoleClassAttributes().put(attributeMap.get(NAME), attributeMap);

            } else if (roleClassNode.getNodeType() == Node.ELEMENT_NODE && VERSION.equals(roleClassNode.getNodeName())) {
                roleClassLib.setVersion(roleClassNode.getFirstChild().getNodeValue());
            }
        }
        roleClassLibMap.put(roleClassLib.getAttributes().get(NAME), roleClassLib);
    }


    private void buildRole(RoleClassLib roleClassLib, String roleClassLibName) {
        // 1.构建文件夹
        String roleClassLibNodeId = generateNodeId();
        Element roleClassLibElement = dom.createElement(UAOBJECT);
        roleClassLibElement.setAttribute(NODEID, roleClassLibNodeId);
        roleClassLibElement.setAttribute(BROWSENAME, roleClassLibName);
        Element displayName = buildBeanValue(DISPLAYNAME, roleClassLibName);
        roleClassLibElement.appendChild(displayName);
        Element refs = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        ref.setTextContent("i=61");
        refs.appendChild(ref);
        Element ref1 = dom.createElement(REFERENCE);
        ref1.setAttribute(REFERENCETYPE, ReferenceEnum.ORGANIZES.getName());
        ref1.setAttribute(ISFORWARD, FALSE);
        ref1.setTextContent("ns=1;i=5009");
        refs.appendChild(ref1);
        for (int j = 0; j < roleClassLibsDirectory.getAttributes().getLength(); j++) {
            if (NODEID.equals(roleClassLibsDirectory.getAttributes().item(j).getNodeName())) {
                String roleClassLibsDirectoryNodeId = roleClassLibsDirectory.getAttributes().item(j).getNodeValue();
                Element ref2 = dom.createElement(REFERENCE);
                ref2.setAttribute(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
                ref2.setAttribute(ISFORWARD, FALSE);
                ref2.setTextContent(roleClassLibsDirectoryNodeId);
                refs.appendChild(ref2);
            }
        }
        roleClassLibElement.appendChild(refs);
        opcUaXml.appendChild(roleClassLibElement);

        buildPropertyNode(VERSION, roleClassLib.getVersion(), roleClassLibNodeId,roleClassLibElement);

        Map<String, String> refAddMap = new HashMap<>();
        refAddMap.put(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        addReference(roleClassLibsDirectory, roleClassLibNodeId, refAddMap);

        // 2.构建RoleClassElement
        for (Map.Entry<String, Map<String, String>> entry : roleClassLib.getRoleClassAttributes().entrySet()) {
            String roleClassName = entry.getKey();
            Map<String, String> roleClassAttributes = entry.getValue();
            Element element = buildObjectTypeXml(roleClassName, roleClassAttributes);
            Map<String,String> addRefMap = new HashMap<>();
            addRefMap.put(REFERENCETYPE,ReferenceEnum.HASSUBTYPE.getName());
            addRefMap.put(ISFORWARD,FALSE);
            addReference(element,"ns=1;i=1001",addRefMap);
            add4002ReferenceToParent(roleClassLibElement,element);
            opcUaXml.appendChild(element);
        }
    }

    private void parseInterfaceClassLib(Node interfaceClassLibNode) {
        NamedNodeMap attributes = interfaceClassLibNode.getAttributes();
        InterfaceClassLib interfaceClassLib = new InterfaceClassLib();
        // 1.属性
        for (int i = 0; i < attributes.getLength(); i++) {
            interfaceClassLib.getAttributes().put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
        // 2.版本和RoleClass
        for (int j = 0; j < interfaceClassLibNode.getChildNodes().getLength(); j++) {
            Node interfaceClassNode = interfaceClassLibNode.getChildNodes().item(j);
            if (interfaceClassNode.getNodeType() == Node.ELEMENT_NODE && INTERFACECLASS.equals(interfaceClassNode.getNodeName())) {
                Map<String, String> attributeMap = new HashMap<>();
                NamedNodeMap interfaceClassNodeAttributes = interfaceClassNode.getAttributes();
                for (int i = 0; i < interfaceClassNodeAttributes.getLength(); i++) {
                    attributeMap.put(interfaceClassNodeAttributes.item(i).getNodeName(), interfaceClassNodeAttributes.item(i).getNodeValue());
                }
                interfaceClassLib.getInterfaceClassAttributes().put(attributeMap.get(NAME), attributeMap);
            } else if (interfaceClassNode.getNodeType() == Node.ELEMENT_NODE && VERSION.equals(interfaceClassNode.getNodeName())) {
                interfaceClassLib.setVersion(interfaceClassNode.getTextContent());
            }
        }
        interfaceClassLibMap.put(interfaceClassLib.getAttributes().get(NAME), interfaceClassLib);
    }

    private void buildInterface(InterfaceClassLib interfaceClassLib, String interfaceName) {
        String interfaceNodeId = generateNodeId();
        Element interfaceElement = dom.createElement(UAOBJECT);
        interfaceElement.setAttribute(NODEID, interfaceNodeId);
        interfaceElement.setAttribute(BROWSENAME, interfaceName);
        Element displayName = buildBeanValue(DISPLAYNAME, interfaceName);
        interfaceElement.appendChild(displayName);
        Element refs = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        ref.setTextContent("i=61");
        refs.appendChild(ref);
        Element ref1 = dom.createElement(REFERENCE);
        ref1.setAttribute(REFERENCETYPE, ReferenceEnum.ORGANIZES.getName());
        ref1.setAttribute(ISFORWARD, FALSE);
        ref1.setTextContent("ns=1;i=5008");
        refs.appendChild(ref1);
        for (int j = 0; j < interfaceClassLibsDirectory.getAttributes().getLength(); j++) {
            if (NODEID.equals(interfaceClassLibsDirectory.getAttributes().item(j).getNodeName())) {
                String interfaceClassLibsDirectoryNodeId = interfaceClassLibsDirectory.getAttributes().item(j).getNodeValue();
                Element ref2 = dom.createElement(REFERENCE);
                ref2.setAttribute(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
                ref2.setAttribute(ISFORWARD, FALSE);
                ref2.setTextContent(interfaceClassLibsDirectoryNodeId);
                refs.appendChild(ref2);
            }
        }
        interfaceElement.appendChild(refs);

        buildPropertyNode(VERSION, interfaceClassLib.getVersion(), interfaceNodeId,interfaceElement);

        Map<String, String> interfaceClassLibRefAddMap = new HashMap<>();
        interfaceClassLibRefAddMap.put(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        addReference(interfaceClassLibsDirectory, interfaceNodeId, interfaceClassLibRefAddMap);

        for (Map.Entry<String, Map<String, String>> entry : interfaceClassLib.getInterfaceClassAttributes().entrySet()) {
            String interfaceClassName = entry.getKey();
            Map<String, String> interfaceClassAttributes = entry.getValue();
            Element element = buildObjectTypeXml(interfaceClassName, interfaceClassAttributes);
            Map<String,String> addRefMap = new HashMap<>();
            addRefMap.put(REFERENCETYPE,ReferenceEnum.HASSUBTYPE.getName());
            addRefMap.put(ISFORWARD,FALSE);
            addReference(element,"ns=1;i=1001",addRefMap);
            add4002ReferenceToParent(interfaceElement, element);
            opcUaXml.appendChild(element);
        }
        opcUaXml.appendChild(interfaceElement);
    }

    private void add4002ReferenceToParent(Element parentElement, Element element) {
        Map<String, String> refAttributeMap = new HashMap<>();
        refAttributeMap.put(REFERENCETYPE, "ns=1;i=4002");
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            if (NODEID.equals(element.getAttributes().item(i).getNodeName())) {
                addReference(parentElement, element.getAttributes().item(i).getNodeValue(), refAttributeMap);
            }
        }
    }

    private void addReferenceToParent(Element parentElement, Element element,String refType) {
        Map<String, String> refAttributeMap = new HashMap<>();
        refAttributeMap.put(REFERENCETYPE,refType);
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            if (NODEID.equals(element.getAttributes().item(i).getNodeName())) {
                addReference(parentElement, element.getAttributes().item(i).getNodeValue(), refAttributeMap);
            }
        }
    }

    private void addReference(Element targetElement, String refValue, Map<String, String> refAttributeMap) {
        for (int j = 0; j < targetElement.getChildNodes().getLength(); j++) {
            if (REFERENCES.equals(targetElement.getChildNodes().item(j).getNodeName())) {
                Element ref = dom.createElement(REFERENCE);
                ref.setTextContent(refValue);
                for (Map.Entry<String, String> entry : refAttributeMap.entrySet()) {
                    ref.setAttribute(entry.getKey(), entry.getValue());
                }
                targetElement.getChildNodes().item(j).appendChild(ref);
            }
        }
    }

    private void parseSystemUnitClassLib(Node systemUnitClassLibNode) {
        NamedNodeMap attributes = systemUnitClassLibNode.getAttributes();
        SystemUnitClassLib systemUnitClassLib = new SystemUnitClassLib();
        // 1.属性
        for (int i = 0; i < attributes.getLength(); i++) {
            systemUnitClassLib.getAttributes().put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
        // 2.版本和RoleClass
        for (int j = 0; j < systemUnitClassLibNode.getChildNodes().getLength(); j++) {
            Node sonOfLib = systemUnitClassLibNode.getChildNodes().item(j);
            if (sonOfLib.getNodeType() == Node.ELEMENT_NODE && SYSTEMUNITCLASS.equals(sonOfLib.getNodeName())) {
                // 解析单个系统单元类，如电动螺丝刀类
                SystemUnitClass systemUnitClass = buildSystemUnitClass(sonOfLib);
                systemUnitClassLib.getSystemUnitClassMap().put(systemUnitClass.getAttributesMap().get(NAME), systemUnitClass);
            } else if (sonOfLib.getNodeType() == Node.ELEMENT_NODE && VERSION.equals(sonOfLib.getNodeName())) {
                systemUnitClassLib.setVersion(sonOfLib.getTextContent());
            }
        }
        systemUnitClassLibMap.put(systemUnitClassLib.getAttributes().get(NAME), systemUnitClassLib);
    }

    private void parseInstanceHierarchy(Node instanceHierarchyNode) {
        NamedNodeMap attributes = instanceHierarchyNode.getAttributes();
        InstanceHierarchy instanceHierarchy = new InstanceHierarchy();
        // 1.属性
        for (int i = 0; i < attributes.getLength(); i++) {
            instanceHierarchy.getAttributes().put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
        String instanceDirectoryId = generateNodeId();
        List<ReferenceElement> references = new LinkedList<>();
        ReferenceElement referenceElement = new ReferenceElement();
        referenceElement.setValue("ns=1;i=5005");
        referenceElement.getAttributes().put(REFERENCETYPE, ReferenceEnum.ORGANIZES.getName());
        referenceElement.getAttributes().put(ISFORWARD, FALSE);
        references.add(referenceElement);
        ReferenceElement referenceElement1 = new ReferenceElement();
        referenceElement1.setValue("ns=2;i=25");
        referenceElement1.getAttributes().put(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        referenceElement1.getAttributes().put(ISFORWARD, FALSE);
        references.add(referenceElement1);
        ReferenceElement referenceElement2 = new ReferenceElement();
        referenceElement2.setValue("i=61");
        referenceElement2.getAttributes().put(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        references.add(referenceElement2);
        Element instanceDirectory = buildElementByCondition(instanceDirectoryId, instanceHierarchy.getAttributes().get(NAME), references);
        // 添加的目录是其引用
        opcUaXml.appendChild(instanceDirectory);
        instanceDirectoryMap.put(instanceHierarchy.getAttributes().get(NAME), instanceDirectory);

        // 2.解析internalElement
        for (int j = 0; j < instanceHierarchyNode.getChildNodes().getLength(); j++) {
            Node internalElementNode = instanceHierarchyNode.getChildNodes().item(j);
            if (internalElementNode.getNodeType() == Node.ELEMENT_NODE && INTERNALELEMENT.equals(internalElementNode.getNodeName())) {
                InternalElement internalElement = buildInternalElement(internalElementNode);
                instanceHierarchy.getInternalElementMap().put(internalElement.getAttributes().get(NAME), internalElement);
            }
        }
        instanceHierarchyMap.put(instanceHierarchy.getAttributes().get(NAME), instanceHierarchy);
    }

    private InternalElement buildInternalElement(Node internalElementNode) {
        NamedNodeMap attributes = internalElementNode.getAttributes();
        InternalElement internalElement = new InternalElement();
        // 1.构造工程类相关
        // 1.1.属性
        for (int i = 0; i < attributes.getLength(); i++) {
            internalElement.getAttributes().put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
        // 1.2.SupportedRoleClass、ExternalInterface、NewAttribute和RoleRequirements
        for (int j = 0; j < internalElementNode.getChildNodes().getLength(); j++) {
            Node sonOfInternalElementNode = internalElementNode.getChildNodes().item(j);
            if (EXTERNALINTERFACE.equals(sonOfInternalElementNode.getNodeName())) {
                for (int i = 0; i < sonOfInternalElementNode.getAttributes().getLength(); i++) {
                    internalElement.getExternalInterfaceAttributes().put(
                            sonOfInternalElementNode.getAttributes().item(i).getNodeName()
                            , sonOfInternalElementNode.getAttributes().item(i).getNodeValue());
                }
            } else if (SUPPORTEDROLECLASS.equals(sonOfInternalElementNode.getNodeName())) {
                for (int i = 0; i < sonOfInternalElementNode.getAttributes().getLength(); i++) {
                    internalElement.getSupportedRoleClassAttributes().put(
                            sonOfInternalElementNode.getAttributes().item(i).getNodeName()
                            , sonOfInternalElementNode.getAttributes().item(i).getNodeValue());
                }
            } else if (ATTRIBUTE.equals(sonOfInternalElementNode.getNodeName())) {
                Map<String, String> attributeMap = new HashMap<>();
                for (int i = 0; i < sonOfInternalElementNode.getAttributes().getLength(); i++) {
                    attributeMap.put(
                            sonOfInternalElementNode.getAttributes().item(i).getNodeName()
                            , sonOfInternalElementNode.getAttributes().item(i).getNodeValue());
                }
                internalElement.getNewAttributeMap().put(attributeMap.get(NAME), attributeMap);
            } else if (ROLEREQUIREMENTS.equals(sonOfInternalElementNode.getNodeName())) {
                for (int i = 0; i < sonOfInternalElementNode.getAttributes().getLength(); i++) {
                    internalElement.getRoleRequirementsAttributes().put(
                            sonOfInternalElementNode.getAttributes().item(i).getNodeName()
                            , sonOfInternalElementNode.getAttributes().item(i).getNodeValue());
                }
            }
        }
        return internalElement;
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
        Element uri1 = dom.createElement("Uri");
        Element uri2 = dom.createElement("Uri");
        uri1.setTextContent("http://opcfoundation.org/UA/AML/");
        uri2.setTextContent("http://www.iosb.fraunhofer.de/Topology.aml");
        namespaceUris.appendChild(uri1);
        namespaceUris.appendChild(uri2);
        opcUaXml.appendChild(namespaceUris);

        // 3.Alias
        appendAliases();

        // 4.Directories
        String topologyNodeId = topologyAmlInit();
        opcUaXml.appendChild(topologyElement);
        buildPropertyNode("CAEXSchemaVersion","2.15",topologyNodeId,topologyElement);
        buildPropertyNode("FileName","Topology.aml",topologyNodeId,topologyElement);

        initInstanceHierarchies(topologyNodeId);
        opcUaXml.appendChild(instanceHierarchiesDirectory);

        initInterfaceClassLibsDirectory(topologyNodeId);
        opcUaXml.appendChild(interfaceClassLibsDirectory);
        initSystemUnitClassLibsDirectory(topologyNodeId);
        opcUaXml.appendChild(systemUnitClassLibsDirectory);
        initRoleClassLibsDirectory(topologyNodeId);
        opcUaXml.appendChild(roleClassLibsDirectory);
    }

    private String topologyAmlInit() {
        topologyElement = dom.createElement(UAOBJECT);
        String nodeId = generateNodeId();
        topologyElement.setAttribute(NODEID, nodeId);
        topologyElement.setAttribute(BROWSENAME, "Topology.aml");
        Element displayElement = buildBeanValue(DISPLAYNAME, "Topology.aml");
        topologyElement.appendChild(displayElement);
        Element refs = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        ref.setTextContent("ns=1;i=1005");
        refs.appendChild(ref);
        Element ref1 = dom.createElement(REFERENCE);
        ref1.setAttribute(REFERENCETYPE, ReferenceEnum.ORGANIZES.getName());
        ref1.setAttribute(ISFORWARD, FALSE);
        ref1.setTextContent("ns=1;i=5006");
        refs.appendChild(ref1);
        topologyElement.appendChild(refs);
        return nodeId;
    }

    private void initInstanceHierarchies(String topologyNodeId) {
        instanceHierarchiesDirectory = dom.createElement(UAOBJECT);
        String instanceHierarchiesDirectoryNode = generateNodeId();
        instanceHierarchiesDirectory.setAttribute(NODEID, instanceHierarchiesDirectoryNode);
        instanceHierarchiesDirectory.setAttribute(BROWSENAME, "InstanceHierarchies");
        instanceHierarchiesDirectory.setAttribute(PARENTNODEID, topologyNodeId);
        Element instanceHierarchiesDirectoryDisplayName = buildBeanValue(DISPLAYNAME, "InstanceHierarchies");
        instanceHierarchiesDirectory.appendChild(instanceHierarchiesDirectoryDisplayName);

        Element refs = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        ref.setAttribute(ISFORWARD, FALSE);
        ref.setTextContent(topologyNodeId);
        refs.appendChild(ref);
        Element ref1 = dom.createElement(REFERENCE);
        ref1.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        ref1.setTextContent("i=61");
        refs.appendChild(ref1);
        instanceHierarchiesDirectory.appendChild(refs);

        Map<String, String> addReferenceMap = new HashMap<>();
        addReferenceMap.put(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        addReference(topologyElement, instanceHierarchiesDirectoryNode, addReferenceMap);
    }

    private void initInterfaceClassLibsDirectory(String topologyNodeId) {
        interfaceClassLibsDirectory = dom.createElement(UAOBJECT);
        String interfaceClassLibsNode = generateNodeId();
        interfaceClassLibsDirectory.setAttribute(NODEID, interfaceClassLibsNode);
        interfaceClassLibsDirectory.setAttribute(BROWSENAME, "InterfaceClassLibs");
        interfaceClassLibsDirectory.setAttribute(PARENTNODEID, topologyNodeId);
        Element interfaceClassLibsDisplayName = buildBeanValue(DISPLAYNAME, "InterfaceClassLibs");
        interfaceClassLibsDirectory.appendChild(interfaceClassLibsDisplayName);

        Element interfaceClassLibsRefs = dom.createElement(REFERENCES);
        Element interfaceClassLibsRef = dom.createElement(REFERENCE);
        interfaceClassLibsRef.setAttribute(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        interfaceClassLibsRef.setAttribute(ISFORWARD, FALSE);
        interfaceClassLibsRef.setTextContent(topologyNodeId);
        interfaceClassLibsRefs.appendChild(interfaceClassLibsRef);
        Element interfaceClassLibsRef1 = dom.createElement(REFERENCE);
        interfaceClassLibsRef1.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        interfaceClassLibsRef1.setTextContent("i=61");
        interfaceClassLibsRefs.appendChild(interfaceClassLibsRef1);
        interfaceClassLibsDirectory.appendChild(interfaceClassLibsRefs);

        Map<String, String> addReferenceMap = new HashMap<>();
        addReferenceMap.put(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        addReference(topologyElement, interfaceClassLibsNode, addReferenceMap);
    }

    private void initSystemUnitClassLibsDirectory(String topologyNodeId) {
        systemUnitClassLibsDirectory = dom.createElement(UAOBJECT);
        String systemUnitClassLibsNodeId = generateNodeId();
        systemUnitClassLibsDirectory.setAttribute(NODEID, systemUnitClassLibsNodeId);
        systemUnitClassLibsDirectory.setAttribute(BROWSENAME, "SystemUnitClassLibs");
        systemUnitClassLibsDirectory.setAttribute(PARENTNODEID, topologyNodeId);
        Element interfaceClassLibsDisplayName = buildBeanValue(DISPLAYNAME, "SystemUnitClassLibs");
        systemUnitClassLibsDirectory.appendChild(interfaceClassLibsDisplayName);

        Element systemUnitClassLibsRefs = dom.createElement(REFERENCES);
        Element systemUnitClassLibsRef = dom.createElement(REFERENCE);
        systemUnitClassLibsRef.setAttribute(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        systemUnitClassLibsRef.setAttribute(ISFORWARD, FALSE);
        systemUnitClassLibsRef.setTextContent(topologyNodeId);
        systemUnitClassLibsRefs.appendChild(systemUnitClassLibsRef);
        Element systemUnitClassLibsRef1 = dom.createElement(REFERENCE);
        systemUnitClassLibsRef1.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        systemUnitClassLibsRef1.setTextContent("i=61");
        systemUnitClassLibsRefs.appendChild(systemUnitClassLibsRef1);
        systemUnitClassLibsDirectory.appendChild(systemUnitClassLibsRefs);

        Map<String, String> addReferenceMap = new HashMap<>();
        addReferenceMap.put(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        addReference(topologyElement, systemUnitClassLibsNodeId, addReferenceMap);
    }

    private void initRoleClassLibsDirectory(String topologyNodeId) {
        roleClassLibsDirectory = dom.createElement(UAOBJECT);
        String roleClassLibsNodeId = generateNodeId();
        roleClassLibsDirectory.setAttribute(NODEID, roleClassLibsNodeId);
        roleClassLibsDirectory.setAttribute(BROWSENAME, "RoleClassLibs");
        roleClassLibsDirectory.setAttribute(PARENTNODEID, topologyNodeId);
        Element roleClassLibsDisplayName = buildBeanValue(DISPLAYNAME, "RoleClassLibs");
        roleClassLibsDirectory.appendChild(roleClassLibsDisplayName);

        Element refs = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        ref.setAttribute(ISFORWARD, FALSE);
        ref.setTextContent(topologyNodeId);
        refs.appendChild(ref);
        Element ref1 = dom.createElement(REFERENCE);
        ref1.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        ref1.setTextContent("i=61");
        refs.appendChild(ref1);
        roleClassLibsDirectory.appendChild(refs);

        Map<String, String> addReferenceMap = new HashMap<>();
        addReferenceMap.put(REFERENCETYPE, ReferenceEnum.HASCOMPONENT.getName());
        addReference(topologyElement, roleClassLibsNodeId, addReferenceMap);
    }

    private void appendAliases() {
        Element aliases = dom.createElement("Aliases");

        Map<String, String> mp = new HashMap<>();
        addAliase(aliases, mp,"Boolean",1);
        addAliase(aliases, mp,"SByte",2);
        addAliase(aliases, mp,"Byte",3);
        addAliase(aliases, mp,"Int16",4);
        addAliase(aliases, mp,"UInt16",5);
        addAliase(aliases, mp,"Int32",6);
        addAliase(aliases, mp,"UInt32",7);
        addAliase(aliases, mp,"Int64",8);
        addAliase(aliases, mp,"UInt64",9);
        addAliase(aliases, mp,"Float",10);
        addAliase(aliases, mp,"Double",11);
        addAliase(aliases, mp,"DateTime",13);
        addAliase(aliases, mp,"String",12);
        addAliase(aliases, mp,"ByteString",15);
        addAliase(aliases, mp,"Guid",14);
        addAliase(aliases, mp,"XmlElement",16);
        addAliase(aliases, mp,"NodeId",17);
        addAliase(aliases, mp,"ExpandedNodeId",18);
        addAliase(aliases, mp,"QualifiedName",20);
        addAliase(aliases, mp,"LocalizedText",21);
        addAliase(aliases, mp,"StatusCode",19);
        addAliase(aliases, mp,"Structure",22);
        addAliase(aliases, mp,"Number",26);
        addAliase(aliases, mp,"Integer",27);
        addAliase(aliases, mp,"UInteger",28);
        addAliase(aliases, mp,"HasComponent",47);
        addAliase(aliases, mp,"HasProperty",46);
        addAliase(aliases, mp,"Organizes",35);
        addAliase(aliases, mp,"HasEventSource",36);
        addAliase(aliases, mp,"HasNotifier",48);
        addAliase(aliases, mp,"HasSubtype",45);
        addAliase(aliases, mp,"HasTypeDefinition",40);
        addAliase(aliases, mp,"HasModellingRule",37);
        addAliase(aliases, mp,"HasEncoding",38);
        addAliase(aliases, mp,"HasDescription",39);
        addAliase(aliases, mp,"Duration",290);

        // 注意，已经定义的对象其实是对象的引用，重新指向新的一个对象，则失去了原对象的操作权力

        opcUaXml.appendChild(aliases);
    }

    private void addAliase(Element aliases, Map<String, String> mp,String alias,int context) {
        mp.put(ALIAS, alias);
        String text = "i=";
        text += String.valueOf(context);

        Element aliasElement = buildBeanAttributeValue(ALIAS, text, mp);
        aliases.appendChild(aliasElement);
    }


    private Element buildObjectTypeXml(String name, Map<String, String> attributeMap) {

        Element element = dom.createElement(UAOBJECTTYPE);
        // 设置属性
        element.setAttribute(BROWSENAME, name);
        String nodeId = generateNodeId();
        attributeMap.put(NODEID, nodeId);

        element.setAttribute(NODEID, nodeId);
        Element displayNameBean = buildBeanValue(DISPLAYNAME, name);
        element.appendChild(displayNameBean);
        Element refs = dom.createElement(REFERENCES);
        element.appendChild(refs);
        return element;
    }

    private SystemUnitClass buildSystemUnitClass(Node systemUnitClassNode) {
        NamedNodeMap attributes = systemUnitClassNode.getAttributes();
        SystemUnitClass systemUnitClass = new SystemUnitClass();
        // 1.构造工程类相关
        // 1.1.属性
        for (int i = 0; i < attributes.getLength(); i++) {
            systemUnitClass.getAttributesMap().put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
        // 1.2.SupportedRoleClass和ExternalInterface
        for (int j = 0; j < systemUnitClassNode.getChildNodes().getLength(); j++) {
            Node sonOfSystemUnitClass = systemUnitClassNode.getChildNodes().item(j);
            if (EXTERNALINTERFACE.equals(sonOfSystemUnitClass.getNodeName())) {
                for (int i = 0; i < sonOfSystemUnitClass.getAttributes().getLength(); i++) {
                    systemUnitClass.getExternalInterfaceAttributes().put(
                            sonOfSystemUnitClass.getAttributes().item(i).getNodeName()
                            , sonOfSystemUnitClass.getAttributes().item(i).getNodeValue());
                }
            } else if (SUPPORTEDROLECLASS.equals(sonOfSystemUnitClass.getNodeName())) {
                for (int i = 0; i < sonOfSystemUnitClass.getAttributes().getLength(); i++) {
                    systemUnitClass.getSupportedRoleClassAttributes().put(
                            sonOfSystemUnitClass.getAttributes().item(i).getNodeName()
                            , sonOfSystemUnitClass.getAttributes().item(i).getNodeValue());
                }
            }
        }
        return systemUnitClass;
    }

    private Element buildSystemUnitClassElement(SystemUnitClass systemUnitClass, Element systemUnitClassLibElement) {

        String name = systemUnitClass.getAttributesMap().get(NAME);

        // 2. 写Element相关
        Element element = dom.createElement(UAOBJECTTYPE);
        // 设置属性
        element.setAttribute(BROWSENAME, name);
        String nodeId = generateNodeId();
        systemUnitClass.getAttributesMap().put(NODEID,nodeId);
        element.setAttribute(NODEID, nodeId);
        // 一级子节点 displayName
        Element displayNameBean = buildBeanValue(DISPLAYNAME, name);
        element.appendChild(displayNameBean);
        // 一级子节点References
        Element refersEle = dom.createElement(REFERENCES);

        Element subTypeRef = dom.createElement(REFERENCE);
        subTypeRef.setAttribute(REFERENCETYPE, ReferenceEnum.HASSUBTYPE.getName());
        subTypeRef.setAttribute(ISFORWARD, FALSE);
        subTypeRef.setTextContent("ns=1;i=1001");
        refersEle.appendChild(subTypeRef);
        // ExternalInterface
        if (systemUnitClass.getExternalInterfaceAttributes().size() > 0) {
            String objectName = systemUnitClass.getExternalInterfaceAttributes().get(NAME);
            String refBaseClassPath = systemUnitClass.getExternalInterfaceAttributes().get(REFBASECLASSPATH);
            String[] refPath = refBaseClassPath.split("/");
            String interfaceClassLibName = refPath[0];
            String interfaceClassName = refPath[1];
            String refNodeId = interfaceClassLibMap.get(interfaceClassLibName).getInterfaceClassAttributes().get(interfaceClassName).get(NODEID);
            // 根据接口创建EnergySupply对象节点，及其引用
            String objectNodeId = buildObjectByInterface(refNodeId, element.getAttribute(NODEID), objectName, systemUnitClass.getExternalInterfaceAttributes());
            //创建ElectricScrewdriver的引用
            Element ref = dom.createElement(REFERENCE);
            ref.setAttribute(REFERENCETYPE, RelationReferenceMapping.getMap().get(RelationEnum.EXTERNALINTERFACE.getName()));
            ref.setTextContent(objectNodeId);
            refersEle.appendChild(ref);
        }

        // SupportedRoleClass
        if (systemUnitClass.getSupportedRoleClassAttributes().size() > 0) {
            String refRoleClassPath = systemUnitClass.getSupportedRoleClassAttributes().get(REFROLECLASSPATH);
            String[] refPath = refRoleClassPath.split("/");
            String roleClassLibName = refPath[0];
            String roleClassName = refPath[1];
            String refNodeId = roleClassLibMap.get(roleClassLibName).getRoleClassAttributes().get(roleClassName).get(NODEID);
            //创建ElectricScrewdriver的引用
            Element ref = dom.createElement(REFERENCE);
            ref.setAttribute(REFERENCETYPE, RelationReferenceMapping.getMap().get(RelationEnum.SUPPORTEDROLECLASS.getName()));
            ref.setTextContent(refNodeId);
            refersEle.appendChild(ref);
        }
        element.appendChild(refersEle);

        Map<String, String> addRefMap = new HashMap<>();
        addRefMap.put(REFERENCETYPE, "ns=1;i=4002");
        addReference(systemUnitClassLibElement, nodeId, addRefMap);
        return element;
    }

    private String buildObjectByInterface(String refNodeId, String parentNodeId, String objectName, Map<String, String> additionalVariable) {
        String nodeId = generateNodeId();
        Element object = dom.createElement(UAOBJECT);
        object.setAttribute(NODEID, nodeId);
        object.setAttribute(BROWSENAME, objectName);
        object.setAttribute(PARENTNODEID, parentNodeId);

        Element displayName = buildBeanValue(DISPLAYNAME, objectName);
        object.appendChild(displayName);

        Element refersEle = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        ref.setTextContent(refNodeId);
        refersEle.appendChild(ref);
        refersEle.appendChild(ref);
        object.appendChild(refersEle);

        for (Map.Entry<String, String> entry : additionalVariable.entrySet()) {
            if (!entry.getKey().equals(NAME) && !entry.getKey().equals(REFBASECLASSPATH)) {
                buildPropertyNode(entry.getKey(), entry.getValue(), nodeId,object);
            }
        }

        opcUaXml.appendChild(object);
        return nodeId;
    }

    private String buildPropertyNode(String name, String value, String parentNodeId, Element parentElement) {
        String nodeId = generateNodeId();

        Element variable = dom.createElement(UAVARIABLE);
        variable.setAttribute(NODEID, nodeId);
        variable.setAttribute(BROWSENAME, name);
        variable.setAttribute(PARENTNODEID, parentNodeId);
        Element displayName = buildBeanValue(DISPLAYNAME, name);
        variable.appendChild(displayName);
        Element valueData;
        HashMap<String, String> valueAttribute = new HashMap<String, String>();
        valueAttribute.put("xmlns", "http://opcfoundation.org/UA/2008/02/Types.xsd");
        if (DataValidate.isInteger(value)) {
            variable.setAttribute(DATATYPE, UADataTypeEnum.INT.getName());
            valueData = buildBeanAttributeValue(UADataTypeEnum.INT.getName(), value, valueAttribute);
        } else if (DataValidate.isDouble(value)) {
            variable.setAttribute(DATATYPE, UADataTypeEnum.DOUBLE.getName());
            valueData = buildBeanAttributeValue(UADataTypeEnum.DOUBLE.getName(), value, valueAttribute);
        } else {
            variable.setAttribute(DATATYPE, UADataTypeEnum.STRING.getName());
            valueData = buildBeanAttributeValue(UADataTypeEnum.STRING.getName(), value, valueAttribute);
        }
        Element valueBean = dom.createElement(VALUE);
        valueBean.appendChild(valueData);
        variable.appendChild(valueBean);

        Element refersEle = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        ref.setTextContent("i=68");
        refersEle.appendChild(ref);
        variable.appendChild(refersEle);
        opcUaXml.appendChild(variable);

        addReferenceToParent(parentElement,variable,ReferenceEnum.HASPROPERTY.getName());
        return nodeId;
    }

    private String buildNewVariableNode(String name, String value, String parentNodeId) {
        String nodeId = generateNodeId();

        Element variable = dom.createElement(UAVARIABLE);
        variable.setAttribute(NODEID, nodeId);
        variable.setAttribute(BROWSENAME, name);
        variable.setAttribute(PARENTNODEID, parentNodeId);
        Element displayName = buildBeanValue(DISPLAYNAME, name);
        variable.appendChild(displayName);
        Element valueData;
        HashMap<String, String> valueAttribute = new HashMap<String, String>();
        valueAttribute.put("xmlns", "http://opcfoundation.org/UA/2008/02/Types.xsd");
        if (DataValidate.isInteger(value)) {
            variable.setAttribute(DATATYPE, UADataTypeEnum.INT.getName());
            valueData = buildBeanAttributeValue(UADataTypeEnum.INT.getName(), value, valueAttribute);
        } else if (DataValidate.isDouble(value)) {
            variable.setAttribute(DATATYPE, UADataTypeEnum.DOUBLE.getName());
            valueData = buildBeanAttributeValue(UADataTypeEnum.DOUBLE.getName(), value, valueAttribute);
        } else {
            variable.setAttribute(DATATYPE, UADataTypeEnum.STRING.getName());
            valueData = buildBeanAttributeValue(UADataTypeEnum.STRING.getName(), value, valueAttribute);
        }
        Element valueBean = dom.createElement(VALUE);
        valueBean.appendChild(valueData);
        variable.appendChild(valueBean);

        Element refersEle = dom.createElement(REFERENCES);
        Element ref = dom.createElement(REFERENCE);
        ref.setAttribute(REFERENCETYPE, ReferenceEnum.HASTYPEDEFINITION.getName());
        ref.setTextContent("i=63");
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

    private Element buildBeanAttributeValue(String bean, String value, Map<String, String> map) {
        Element element = dom.createElement(bean);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            element.setAttribute(entry.getKey(), entry.getValue());
        }
        element.setTextContent(value);
        return element;
    }

    private String generateNodeId() {
        return String.format("ns=%s;i=%s", ns, i++);
    }

    private void exportElementToFile(Element element, String fileName) throws TransformerException {
        //将现有结构转换为xml文件
        //创建TransformerFactory对象
        TransformerFactory tff = TransformerFactory.newInstance();
        //创建Transformer对象
        Transformer tf = tff.newTransformer();
        //设置换行
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        //调用Transformer对象的transform方法，创建xml文件
        tf.transform(new DOMSource(element), new StreamResult(new File(".\\XmlParserService\\src\\main\\resources\\" + fileName)));
    }

    Element buildElementByCondition(String nodeId, String name, List<ReferenceElement> referenceElementList) {
        Element objectElement = dom.createElement(UAOBJECT);
        objectElement.setAttribute(NODEID, nodeId);
        objectElement.setAttribute(BROWSENAME, name);
        Element displayName = dom.createElement(DISPLAYNAME);
        displayName.setTextContent(name);
        objectElement.appendChild(displayName);
        Element refs = dom.createElement(REFERENCES);

        for (ReferenceElement referenceElement : referenceElementList) {
            Element ref = dom.createElement(REFERENCE);
            ref.setTextContent(referenceElement.getValue());
            for (Map.Entry<String, String> entry : referenceElement.getAttributes().entrySet()) {
                ref.setAttribute(entry.getKey(), entry.getValue());
            }
            refs.appendChild(ref);
        }
        objectElement.appendChild(refs);
        return objectElement;
    }
}
