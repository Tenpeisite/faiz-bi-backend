package com.zhj.bi.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhj
 * @version 1.0
 * @description
 * @date 2024/2/24 16:09
 */
//@SpringBootTest
public class ChartServiceTest {

    @Test
    public void test() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:网站数据.xlsx");
        List<Map<Integer, String>> list = EasyExcel.read(file)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();

        //读表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerList = headerMap.values().stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());

        StringBuilder createSql = new StringBuilder();
        createSql.append("CREATE TABLE ");
        String tableName = String.format("chart_%s", "111");
        createSql.append(tableName).append("\n");
        String format = "VARCHAR(255) NULL DEFAULT NULL";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id BIGINT(20) AUTO_INCREMENT,").append("\n");
        for (String headerName : headerList) {
            stringBuilder.append(headerName).append(" ").append(format).append(",").append("\n");
        }
        stringBuilder.append("isDelete tinyint default 0 not null comment '是否删除',").append("\n");
        stringBuilder.append("PRIMARY KEY (`id`)");

        createSql.append(" (").append("\n");
        createSql.append(stringBuilder).append("\n");
        createSql.append(");");

        String sql = createSql.toString();
        System.out.println(sql);
        System.out.println("-------------");
        StringBuilder insertSql = new StringBuilder();
        insertSql.append("INSERT INTO ").append(tableName).append("(").append(StringUtils.join(headerList,",")).append(")").append("\n");
        insertSql.append("VALUES ").append("\n");
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> datas = (LinkedHashMap) list.get(i);
            List<String> line = datas.values().stream().map(data -> "'" + data + "'").collect(Collectors.toList());
            if(i==list.size()-1){
                insertSql.append("(").append(StringUtils.join(line, ",")).append(");");
                break;
            }
            insertSql.append("(").append(StringUtils.join(line, ",")).append("),").append("\n");
        }

        System.out.println(insertSql.toString());

    }
}
