package com.zhj.bi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhj.bi.common.ErrorCode;
import com.zhj.bi.exception.BusinessException;
import com.zhj.bi.exception.ThrowUtils;
import com.zhj.bi.manager.AiManager;
import com.zhj.bi.model.dto.chart.GenChartByAiRequest;
import com.zhj.bi.model.dto.chart.SaveChartDTO;
import com.zhj.bi.model.entity.Chart;
import com.zhj.bi.model.vo.BiResponse;
import com.zhj.bi.service.ChartService;
import com.zhj.bi.mapper.ChartMapper;
import com.zhj.bi.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 鏈辩剷鏉�
 * @description 针对表【chart(图表信息表)】的数据库操作Service实现
 * @createDate 2023-12-22 21:16:46
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService {

    @Resource
    private AiManager aiManager;

    @Resource
    private ChartMapper chartMapper;

    @Override
    public StringBuilder getUserInput(MultipartFile multipartFile, String goal, String chartType, String csvData) {
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        //拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "\n请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n").append(csvData);
        return userInput;
    }

    @Override
    public void verifyFile(MultipartFile multipartFile, String name, String goal) {
        //校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        //校验文件
        long size = multipartFile.getSize();
        final long ONE_MB = 1024 * 1024;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过1MB");
        //校验后缀
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "csv");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
    }

    @Override
    public BiResponse saveChartInfo(SaveChartDTO saveChartDTO) {

        Chart chart = new Chart();
        chart.setGoal(saveChartDTO.getGoal());
        chart.setName(saveChartDTO.getName());
        //chart.setChartData(csvData);
        chart.setChartType(saveChartDTO.getChartType());
        chart.setGenChart(saveChartDTO.getGenChart());
        chart.setGenResult(saveChartDTO.getGenResult());
        chart.setUserId(saveChartDTO.getUserId());
        chart.setStatus(saveChartDTO.getStatus());
        boolean flag = save(chart);
        ThrowUtils.throwIf(!flag, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        //返回结果
        return new BiResponse(chart.getId(), saveChartDTO.getGenChart(), saveChartDTO.getGenResult());
    }

    @Override
    public String[] doChat(StringBuilder userInput) {
        long biModelId = 1659171950288818178L;
        String result = aiManager.doChat(biModelId, userInput.toString());
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        return splits;
    }

    @Override
    public void createChart(List<Map<Integer, String>> dataList, Long chartId) {
        //读表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) dataList.get(0);
        List<String> headerList = headerMap.values().stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());

        StringBuilder createSql = new StringBuilder();
        createSql.append("CREATE TABLE ");
        String tableName = String.format("chart_%s", chartId);
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

        try {
            //构建自定义sql，开始建表
            chartMapper.createTableByExcelData(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //建表成功，开始 读取/插入数据 拼凑插入sql
        StringBuilder insertSql = new StringBuilder();
        insertSql.append("INSERT INTO ").append(tableName).append("(").append(StringUtils.join(headerList, ",")).append(")").append("\n");
        insertSql.append("VALUES ").append("\n");
        for (int i = 1; i < dataList.size(); i++) {
            LinkedHashMap<Integer, String> datas = (LinkedHashMap) dataList.get(i);
            List<String> line = datas.values().stream().map(data -> "'" + data + "'").collect(Collectors.toList());
            if (i == dataList.size() - 1) {
                insertSql.append("(").append(StringUtils.join(line, ",")).append(");");
                break;
            }
            insertSql.append("(").append(StringUtils.join(line, ",")).append("),").append("\n");
        }

        //构建自定义sql，插入数据
        chartMapper.insertChart(insertSql.toString());
    }


}




