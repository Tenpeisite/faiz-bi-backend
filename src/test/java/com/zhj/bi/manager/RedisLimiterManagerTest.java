package com.zhj.bi.manager;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    void doRateLimiter() {
        String userId="1";
        for (int i = 0; i < 10; i++) {
            try {
                redisLimiterManager.doRateLimiter(userId);
                System.out.println("成功");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("失败");
            }
        }
    }
}