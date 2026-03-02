package com.hy.demo.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    @PostMapping("/clean")
    public String cleanAiText(@RequestBody String text){
        String[] sentences = text.split("[。！？.!?]");
        StringBuilder result = new StringBuilder();
        int count = 1;
        for (String sentence : sentences) {
            sentence = sentence.trim();

            if (sentence.isEmpty())
                continue;

            if (sentence.contains("需要我帮你") || sentence.contains("可以吗"))
                continue;

            if (sentence.matches("^[一二三四五六七八九十]+、.*") || sentence.matches("^\\d+\\..*")) {
                result.append("\n").append(sentence).append("\n");
                continue;
            }

            if (sentence.matches("^\\d+\\.?$"))
                continue;

            if (sentence.length() <= 2)
                continue;

            if (sentence.length() > 5){
                result.append(count++).append(". ").append(sentence).append("\n");
            }
        }

        return result.toString();
    }
}
