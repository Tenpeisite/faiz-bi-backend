package com.zhj.bi.model.dto.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.convert.DataSizeUnit;

/**
 * @author zhj
 * @version 1.0
 * @description
 * @date 2024/2/24 15:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveChartDTO {

    private String goal;
    private String name;
    private String chartType;
    private Long userId;
    private String status;
    private String genChart;
    private String genResult;

}
