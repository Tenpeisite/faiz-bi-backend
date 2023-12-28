package com.zhj.bi.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhj
 * @version 1.0
 * @description
 * @date 2023/12/27 17:38
 */
@Data
public class GenChartByAiRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

    private static final long serialVersionUID = 1L;
}
