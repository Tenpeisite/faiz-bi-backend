package com.zhj.bi.manager;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import com.zhj.bi.common.ErrorCode;
import com.zhj.bi.exception.BusinessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author zhj
 * @version 1.0
 * @description
 * @date 2023/12/28 23:14
 */
@Service
public class AiManager {

    @Resource
    private YuCongMingClient yuCongMingClient;

    /**
     * AI 对话
     *
     * @param message
     * @return
     */
    public String doChat(Long modelId, String message) {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = null;
        Retryer<BaseResponse<DevChatResponse>> retryer = RetryerBuilder.<BaseResponse<DevChatResponse>>newBuilder()
                //无论出现什么异常都重试
                .retryIfException()
                //返回结果不为正确格式时重试,数组长度要求为3
                .retryIfResult(res -> checkFormat(res))
                //重试等待策略：等待 1s 后再进行重试
                //.withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                //重试停止策略：重试达到3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("RetryListener：第" + attempt.getAttemptNumber() + "次调用");
                    }
                })
                .build();

        try {
            response = retryer.call(() -> yuCongMingClient.doChat(devChatRequest));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }

        return response.getData().getContent();
    }

    private boolean checkFormat(BaseResponse<DevChatResponse> response){
        String result = response.getData().getContent();
        String[] splits = null;
        try {
            splits = result.split("【【【【【");
        } catch (Exception e) {
            return true;
        }
        return splits.length < 3;
    }
}
