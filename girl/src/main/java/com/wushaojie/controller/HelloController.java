package com.wushaojie.controller;

import com.wushaojie.domain.Girl;
import com.wushaojie.properties.GirlProperties;
import com.wushaojie.repository.GirlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private GirlRepository girlRepository;

    @Value("${cupSize}")
    private String cupSize;
    @Autowired
    private GirlProperties girlProperties;

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String hello(){
        return "hello World";
    }

    @RequestMapping(value = "/cupSize",method = RequestMethod.GET)
    public String cupSize(){
        return cupSize;
    }

    @RequestMapping(value = "/girlProperties",method = RequestMethod.GET)
    public String girlProperties(){
        return girlProperties.getAge();
    }

    @GetMapping(value = "/girls")
    public List<Girl> findGirls(){
        return girlRepository.findAll();
    }

    @GetMapping(value = "/age/{age}")
    public List<Girl> findByAge(@PathVariable(value = "age") Integer age){
        return girlRepository.findByAge(age);
    }
}
