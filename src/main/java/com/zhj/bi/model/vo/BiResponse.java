package com.zhj.bi.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhj
 * @version 1.0
 * @description bi的返回结果
 * @date 2023/12/28 23:45
 */
@Data
@AllArgsConstructor
public class BiResponse {

    private Long chartId;

    private String genChart;

    private String genResult;
}
