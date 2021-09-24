package org.imc.service;

import org.springframework.stereotype.Component;

@Component
public class HelloService {
    public String sayHello(String username) {
        return "hello" + username;
    }
}
