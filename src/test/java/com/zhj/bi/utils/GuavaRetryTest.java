package com.zhj.bi.utils;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author zhj
 * @version 1.0
 * @description
 * @date 2024/1/11 18:04
 */
//@SpringBootTest
public class GuavaRetryTest {


    @Test
    public void guavaRetry() {
        Retryer<String> retryer = RetryerBuilder.<String>newBuilder()
                //无论出现什么异常都重试
                .retryIfException()
                //返回结果为 error 时，进行重试
                .retryIfResult(result -> Objects.equals(result, "error"))
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
            retryer.call(() -> testGuavaRetry());
        } catch (Exception e) {
            throw new RuntimeException("终止");
        }
        System.out.println("正常调用");

    }

    public String testGuavaRetry() {
        return "error";
    }

}
