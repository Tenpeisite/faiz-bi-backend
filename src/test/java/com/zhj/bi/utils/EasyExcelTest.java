package com.zhj.bi.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * EasyExcel 测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
public class EasyExcelTest {

    @Test
    public void doImport() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:test_excel.xlsx");
        List<Map<Integer, String>> list = EasyExcel.read(file)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        System.out.println(list);
    }

    @Test
    public void testExcelSize() throws FileNotFoundException {
        List<Map<Integer, String>> list = readExcelData();
        StringBuilder stringBuilder = new StringBuilder();
        //读表头
        LinkedHashMap<Integer, String> headMap = (LinkedHashMap) list.get(0);
        //拼接表头
        stringBuilder.append(StringUtils.join(headMap.values(), ",")).append("\n");
        //读数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> datas = (LinkedHashMap) list.get(i);
            stringBuilder.append(StringUtils.join(datas.values(), ",")).append("\n");
        }
        System.out.println(stringBuilder.toString());
    }

    @Test
    public void testCsvSize() throws FileNotFoundException {
        List<Map<Integer, String>> list = readExcelData();
        StringBuilder stringBuilder = new StringBuilder();
        //读表头
        LinkedHashMap<Integer, String> headMap = (LinkedHashMap) list.get(0);
        //拼接表头
        stringBuilder.append(StringUtils.join(headMap.values(), ",")).append("\n");
        //读数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> datas = (LinkedHashMap) list.get(i);
            stringBuilder.append(StringUtils.join(datas.values(), ",")).append("\n");
        }
        System.out.println(stringBuilder.toString());
    }

    private List<Map<Integer, String>> readExcelData() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:网站数据.xlsx");
        List<Map<Integer, String>> list = EasyExcel.read(file)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        return list;
    }

}