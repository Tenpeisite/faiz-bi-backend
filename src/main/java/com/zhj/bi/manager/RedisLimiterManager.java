package com.zhj.bi.manager;

import com.zhj.bi.common.ErrorCode;
import com.zhj.bi.exception.ThrowUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhj
 * @version 1.0
 * @description 专门提供 RedisLimiter 限流基础服务（提供一个通用的能力）
 * @date 2023/12/30 0:19
 */
@Component
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRateLimiter(String key){
        // 创建限流器，每秒钟只能操作2次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        //每当一个操作过来的时候，根据用户权限不同拿不同数量的令牌
        //这样就能实现会员能够调用频率更高，非会员调用频率更低
        //if(会员){
        //    boolean acquireResult = rateLimiter.tryAcquire(1);
        //}else{
        //    boolean acquireResult = rateLimiter.tryAcquire(5);
        //}

        //操作是否成功
        boolean canOp = rateLimiter.tryAcquire(1);
        //不成功则抛出异常
        ThrowUtils.throwIf(!canOp, ErrorCode.TOO_MANY_REQUEST);
    }
}
