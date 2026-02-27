package com.hy.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/json")
public class JsonController {

    @PostMapping("/format")
    public String format(@RequestBody String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
//            Object obj = mapper.readValue(json, Object.class);
            JsonNode node = mapper.readTree(json);
            if (!node.isObject() && !node.isArray()){
                // node不是一个对象或数组
                return "JSON格式错误，请输入完整的JSON对象或数组";
            }
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        }catch (Exception e){
            return "JSON格式错误，请检查输入内容";
        }
    }

}
