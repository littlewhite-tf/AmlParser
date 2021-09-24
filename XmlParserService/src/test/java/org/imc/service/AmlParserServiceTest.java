package org.imc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
/**
 * 从XmlParserService所在包从上往下扫描bean
 */
@SpringBootTest(classes = AmlParserService.class)
@RunWith(SpringRunner.class)
public class AmlParserServiceTest {

    @Autowired
    private AmlParserService amlParserService;

    @Test
    public void parseAml() {
        amlParserService.parseAml("");
    }
}