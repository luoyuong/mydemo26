package com.hy.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {


    @GetMapping("/hello")
    public String hello() {
        return "我开始做自己的产品了";
    }

    @GetMapping("/count")
    public Integer count(@RequestParam("text") String text) {
        return  text.length();
    }
}
