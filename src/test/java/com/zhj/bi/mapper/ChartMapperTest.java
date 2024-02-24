package com.zhj.bi.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author zhj
 * @version 1.0
 * @description
 * @date 2024/2/24 17:21
 */
@SpringBootTest
public class ChartMapperTest {

    @Resource
    private ChartMapper chartMapper;

    private static final String createTableSql="CREATE TABLE chart_111\n" +
            " (\n" +
            "id BIGINT(20) AUTO_INCREMENT,\n" +
            "日期 VARCHAR(255) NULL DEFAULT NULL,\n" +
            "用户数 VARCHAR(255) NULL DEFAULT NULL,\n" +
            "isDelete tinyint default 0 not null comment '是否删除',\n" +
            "PRIMARY KEY (`id`)\n" +
            ");";

    private static final  String insertSql="INSERT INTO chart_111(日期,用户数)\n" +
            "VALUES \n" +
            "('1号','10'),\n" +
            "('2号','20'),\n" +
            "('3号','30'),\n" +
            "('4号','90'),\n" +
            "('5号','0'),\n" +
            "('6号','10'),\n" +
            "('7号','20');";


    @Test
    public void testCreatTable(){
        chartMapper.createTableByExcelData(createTableSql);
    }

    @Test
    public void testInsertDatas(){
        chartMapper.insertChart(insertSql);
    }
}
