package org.imc.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Component
public class XmlParserService {
    public static String parseXml(String path) {
        String result = parse(".\\src\\main\\resources\\parser_example.xml");
        return result;
    }

    private static String parse(String path) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.parse(path);
            NodeList sList = d.getElementsByTagName("UANodeSet");
            //element和node两种方式都可以解析
            //element(sList);
            //node(sList);
            elementSearch(sList.item(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void elementSearch(Node root) {
        //1.获取节点值
        if (root.getFirstChild() != null) {
            System.out.println(root.getNodeName() + ":" + root.getFirstChild().getNodeValue());
        } else {
            System.out.println(root.getNodeName() + ":");
        }

        //2.获取节点属性
        NamedNodeMap nodeMap = root.getAttributes();
        for (int i = 0; i < nodeMap.getLength(); i++) {
            System.out.println(nodeMap.item(i).getNodeName() + ":" + nodeMap.item(i).getNodeValue());
        }

        NodeList childNodes = root.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            //递归所有元素子节点
            if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                /**
                 * 比如UANodeSet的子标签有NamespaceUris、Aliases等9个元素节点,却一共19个子节点，如下
                 0:#text
                 1:NamespaceUris
                 2:#text
                 3:Aliases
                 4:#text
                 5:Extensions
                 6:#text
                 7:UAObjectType
                 8:#text
                 9:UAMethod
                 10:#text
                 11:UAVariable
                 12:#text
                 13:UAVariable
                 14:#text
                 15:UAVariable
                 16:#text
                 17:UAVariable
                 18:#text
                 */
                elementSearch(childNodes.item(j));
            }
        }
    }

    public static void node(NodeList list) {
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    System.out.print(childNodes.item(j).getNodeName() + ":");
                    System.out.println(childNodes.item(j).getFirstChild().getNodeValue());
                }
            }
        }
    }

    public static void element(NodeList list) {
        //比如标签UANodeSet只有一个，那么list.getLength()=1
        for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            NodeList childNodes = element.getChildNodes();

            // System.out.println(childNodes.getLength());
            for (int j = 0; j < childNodes.getLength(); j++) {
                //System.out.println(j+":"+childNodes.item(j).getNodeName());
                if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {//获取所有9个元素节点
                    //获取节点
                    System.out.print(childNodes.item(j).getNodeName() + ":");
                    //获取节点值
                    System.out.println(childNodes.item(j).getFirstChild().getNodeValue());
                    elementSearch(childNodes.item(j));
                }
            }
        }
    }



}
