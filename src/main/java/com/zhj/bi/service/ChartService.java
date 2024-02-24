package com.zhj.bi.service;

import com.zhj.bi.model.dto.chart.GenChartByAiRequest;
import com.zhj.bi.model.dto.chart.SaveChartDTO;
import com.zhj.bi.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhj.bi.model.vo.BiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* @author 鏈辩剷鏉�
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-12-22 21:16:46
*/
public interface ChartService extends IService<Chart> {

    StringBuilder getUserInput(MultipartFile multipartFile, String goal, String chartType,String csvData);

    void verifyFile(MultipartFile multipartFile,String name,String goal);

    BiResponse  saveChartInfo(SaveChartDTO saveChartDTO);

    String[] doChat(StringBuilder userInput);

    void createChart(List<Map<Integer, String>> dataList, Long chartId);
}
