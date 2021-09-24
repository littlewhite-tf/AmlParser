package org.imc.dubbo.impl;

import org.imc.dubbo.AmlParserRemoteService;
import org.imc.service.AmlParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@Service(version="${service.version}",interfaceClass = AmlParserRemoteService.class)
@Controller
@RequestMapping("/")
public class AmlParserRemoteServiceImpl implements AmlParserRemoteService {

    @Autowired
    private AmlParserService amlParserService;

    @Override
    @GetMapping("/parse")
    public void parseXml(String path) {
        amlParserService.parseAml("");
        return;
    }
}