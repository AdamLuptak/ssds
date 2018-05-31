package com.here.owc;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "com.here.owc")
public class ApplicationConfiguration {

    @Bean
    public String helloWorld(){
        return "Trigerol mal sns";
    }

}
