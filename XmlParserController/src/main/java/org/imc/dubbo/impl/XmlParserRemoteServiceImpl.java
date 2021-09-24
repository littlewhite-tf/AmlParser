package org.imc.dubbo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.imc.dubbo.HelloRemoteService;
import org.imc.dubbo.XmlParserRemoteService;
import org.imc.service.HelloService;
import org.imc.service.XmlParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(version="${service.version}",interfaceClass = XmlParserRemoteService.class)
public class XmlParserRemoteServiceImpl implements XmlParserRemoteService {

    @Autowired
    private XmlParserService xmlParserService;

    @Override
    public String parseXml(String path) {
        return xmlParserService.parseXml(path);
    }
}