package com.zhj.bi.mq;

import com.rabbitmq.client.Channel;
import com.zhj.bi.common.ErrorCode;
import com.zhj.bi.constant.BiMqConstant;
import com.zhj.bi.exception.BusinessException;
import com.zhj.bi.manager.AiManager;
import com.zhj.bi.model.entity.Chart;
import com.zhj.bi.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhj
 * @version 1.0
 * @description
 * @date 2023/12/31 22:04
 */
@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    //@SneakyThrows
    //@RabbitListener(queues = {"test"}, ackMode = "MANUAL")
    //public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) {
    //    log.info("receiveMessage message = {}", message);
    //    //手动回复ack
    //    channel.basicAck(deliverTag, false);
    //}

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = BiMqConstant.BI_QUEUE_NAME),
            exchange = @Exchange(name = BiMqConstant.BI_EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
            key = BiMqConstant.BI_ROUTING_KEY),
            ackMode = "MANUAL")
    public void receiveBiMessage(String chartId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) {
        log.info("receiveMessage message:{}",chartId);
        if (StringUtils.isBlank(chartId)) {
            //返回nack
            channel.basicNack(deliverTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        Long id = Long.parseLong(chartId);
        //先修改图表任务状态为“执行中”,等执行成功后，修改为“已完成”，保存执行结果。执行失败后，状态修改为失败，记录任务失败信息。
        Chart updateChart = new Chart();
        updateChart.setId(id);
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            handleChartUpdateError(id, "更新图表状态为running操作失败");
            return;
        }

        //获得对应图表
        Chart chart = chartService.getById(id);
        if (chart == null) {
            //返回nack
            channel.basicNack(deliverTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }

        //构造用户输入
        StringBuilder userInput = buildUserInput(chart);

        long biModelId = 1659171950288818178L;
        //调用 AI
        String result = aiManager.doChat(biModelId, userInput.toString());
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            handleChartUpdateError(id, "AI 生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        updateChart.setStatus("succeed");
        updateChart.setGenChart(genChart);
        updateChart.setGenResult(genResult);
        boolean chartResult = chartService.updateById(updateChart);
        if (!chartResult) {
            handleChartUpdateError(id, "更新图表状态为succeed操作失败");
            return;
        }

        //手动ack
        channel.basicAck(deliverTag, false);
    }

    @NotNull
    private StringBuilder buildUserInput(Chart chart) {

        //分析需求：
        //分析网站用户的增长情况
        //原始数据：
        //日期，用户数
        //1号,10
        //2号,20
        //3号,30

        //构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        //拼接分析目标
        String userGoal = chart.getGoal();
        if (StringUtils.isNotBlank(chart.getChartType())) {
            userGoal += ",请使用" + chart.getChartType();
        }
        userInput.append(userGoal).append("\n");
        String csvData = chart.getChartData();
        userInput.append("原始数据：").append("\n").append(csvData);
        return userInput;
    }

    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateCHartResult = new Chart();
        updateCHartResult.setId(chartId);
        updateCHartResult.setStatus("failed");
        updateCHartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateCHartResult);
        if (!updateResult) {
            log.error("更新图表失败状态,chartId:" + chartId + ",错误信息：" + execMessage);
        }
    }
}
