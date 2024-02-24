package com.zhj.bi.model.dto.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author zhj
 * @version 1.0
 * @description
 * @date 2024/2/24 16:30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExcelVO {

    private String csvData;
    private List<Map<Integer, String>> dataList;
}
