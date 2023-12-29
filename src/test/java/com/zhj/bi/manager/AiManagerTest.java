package com.zhj.bi.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {
        String res = aiManager.doChat(1651468516836098050L, "邓紫棋");
        System.out.println(res);
    }
}