package org.imc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 从XmlParserService所在包从上往下扫描bean
 */
@SpringBootTest(classes = XmlParserService.class)
@RunWith(SpringRunner.class)
public class XmlParserServiceTest {

    @Autowired
    private XmlParserService xmlParserService;
    @Test
    public void parseXml() {
        xmlParserService.parseXml("");
    }
}