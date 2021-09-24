package org.imc.dubbo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.imc.dubbo.HelloRemoteService;
import org.imc.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(version="${service.version}",interfaceClass = HelloRemoteService.class)
public class HelloRemoteServiceImpl implements HelloRemoteService {

    @Autowired
    private HelloService helloService;


    @Override
    public String sayHello(String username) {
        System.out.println("被远程调用了");
        return helloService.sayHello(username);
    }
}