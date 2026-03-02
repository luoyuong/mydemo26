package com.hy.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/excel")
public class ExcelController {


    @PostMapping("/to-sql")
    public String toSql(@RequestParam("tablename") String tablename, @RequestBody String text) {
        String[] lines = text.split("\n");
        StringBuffer sql = new StringBuffer();
        for (String line : lines){
            if (line.trim().isEmpty())
                continue;

            String[] arr = line.split(",",-1);
            sql.append("INSERT INTO ").append(tablename).append(" VALUES(");

            for (int i = 0; i < arr.length; i++) {
                String value = arr[i].trim();
                if (value.isEmpty()){
                    sql.append("NULL");
                }else if (value.matches("-?\\d+(\\.\\d+)?")){
                    sql.append(value);
                }else {
                    sql.append("'").append(value).append("'");
                }

                if (i != arr.length - 1){
                    sql.append(",");
                }

            }
            sql.append(");\n");
        }
        return sql.toString();
    }
}
