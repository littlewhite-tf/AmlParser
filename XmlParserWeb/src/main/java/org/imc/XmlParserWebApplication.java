package org.imc;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.imc.service.AmlParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableDubboConfiguration
@SpringBootApplication
@ComponentScan(basePackages = {"org.imc.*"})
public class XmlParserWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(XmlParserWebApplication.class, args);
    }
}
