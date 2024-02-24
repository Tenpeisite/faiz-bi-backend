package com.zhj.bi.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author zhj
 * @version 1.0
 * @description
 * @date 2023/12/30 22:18
 */
@Configuration
@Slf4j
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("线程" + count);
                count++;
                return thread;
            }
        };
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
                4,
                100,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4),
                threadFactory,
                myRejectedHandler());
        return threadPoolExecutor;
    }

    // 自定义拒绝策略
    @Bean("myRejectedHandler")
    public RejectedExecutionHandler myRejectedHandler() {
        return (Runnable r, ThreadPoolExecutor executor) -> {
            if (r != null) {
                log.info("线程任务被拒绝");
                // 尝试重新提交
                try {
                    Thread.sleep(3000);
                    log.info("线程尝试重新提交任务");
                    executor.execute(r);
                } catch (InterruptedException e) {
                    log.error("重新提交线程任务-- 系统休眠异常");
                }
            }
        };
    }
}
