package com.hy.demo.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @PostMapping(value = "/sql",produces = "text/plain;charset=UTF-8")
    public ResponseEntity<byte[]> excelToSql(@RequestParam("file") MultipartFile file, @RequestParam("tablename") String tablename, @RequestParam("mapping") String mapping) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空".getBytes(StandardCharsets.UTF_8));
        }
        if (tablename.isEmpty()){
            return ResponseEntity.badRequest().body("表名不能为空".getBytes(StandardCharsets.UTF_8));
        }

        String sql = generateSql(file, mapping, tablename);
        byte[] data = sql.getBytes(StandardCharsets.UTF_8);


        return ResponseEntity.ok()
                .header("Content-Disposition","attachment; filename=data.sql")
                .header("Content-Type","application/octet-stream")
                .body(data);
    }

    private String generateSql(MultipartFile file,String mapping,String tablename) throws IOException {
        StringBuilder sb = new StringBuilder();
        Workbook wb = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = wb.getSheetAt(0);

        Map<String, String> map = parseMapping(mapping);

        Row headRow = sheet.getRow(0);
        List<String> columns = new ArrayList<>();
        for (Cell cell : headRow) {
            String cellValue = getCellValue(cell);
            columns.add(map.getOrDefault(cellValue,cellValue));
        }
        String columnStr = String.join(",", columns);


        for (int i = 1 ; i <= sheet.getLastRowNum() ; i++) {
            Row row = sheet.getRow(i);
            if (isRowEntity(row)){
                continue;
            }
            int lastCellNum = row.getLastCellNum();
            List<String> values = new ArrayList<>();
            for (int j = 0; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                String value = getCellValue(cell);
                if (value == null || value.isEmpty()){
                    values.add("NULL");
                }else if (value.matches("-?\\d+(\\.\\d+)?")){
                    values.add(value);
                }else {
                    values.add("'"+value+"'");
                }
            }
            String valueStr = String.join(",", values);
            String sql = "INSERT INTO "+tablename+"( "+columnStr+" ) VALUES("+valueStr+")";
            sb.append(sql);
            sb.append(";\n");
        }

        wb.close();
        return sb.toString();
    }


    private boolean isRowEntity(Row row){
        if (row == null){
            return true;
        }

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !getCellValue(cell).trim().isEmpty()){
                // 一行数据中只要有一个，那么就是有数据
                return false;
            }

        }

        return true;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()){
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return "";
        }
    }


    private Map<String,String> parseMapping(String mapping){
        Map<String,String> map = new HashMap<>();
        if (mapping == null || mapping.isEmpty()){
            return map;
        }

        String[] pairs = mapping.split(",");
        for (String pair : pairs){
            String[] arr = pair.split(":");
            if (arr.length == 2){
                map.put(arr[0].trim(), arr[1].trim());
            }

        }

        return map;
    }
}
