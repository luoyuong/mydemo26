package com.hy.demo.controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@RestController
@RequestMapping("/text")
public class TextController {

    @PostMapping("/count")
    public Integer count(@RequestBody String text){
        return text.length();
    }


    @PostMapping("/remove-empty")
    public String removeEmpty(@RequestBody String text){
        String result = text.trim();
        result = result.replaceAll("(?m)^\\s*$\\n","");

        return result;
    }

    @PostMapping("remove-duplicate")
    public String removeDuplicate(@RequestBody String text){
        String result = "";
        String[] lines = text.split("\\r?\\n");
        Set<String> set = new LinkedHashSet<>();
        for (String line : lines) {
            if (StringUtils.isEmpty(line)){
                continue;
            }
            set.add(line);
        }
        result = String.join("\n", set);
        return result;
    }
}
