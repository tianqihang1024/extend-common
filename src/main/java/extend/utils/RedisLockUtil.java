package extend.utils;

import com.alibaba.fastjson.JSON;
import extend.exception.ExtendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 田奇杭
 * @Description 非阻塞的分布式锁
 * @Date 2022/9/10 17:47
 */
@Slf4j
@Component
public class RedisLockUtil implements ApplicationContextAware {

    private static RedisTemplate<String, String> redisTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (redisTemplate == null) {
            RedisLockUtil.redisTemplate = (RedisTemplate<String, String>) applicationContext.getBean("redisTemplate");
        }
    }

    /**
     * 随机数，构成所持有者信息的一部分
     */
    private static final String RANDOM = UUID.randomUUID().toString();

    /**
     * 加锁
     * 一行代码一行注释，自行联想
     * key 尚未被抢占
     *      使用 hash 存储锁信息 key 持有锁的线程 重入次数
     *      设置传入的过期时间
     *      返回 true
     *      终止
     * key 的所有者是当前线程
     *      重入次数 +1
     *      设置传入的过期时间
     *      返回 true
     *      终止
     * 抢占锁失败返回 false
     */
    private static final String LOCK_LUA = "if (redis.call('exists', KEYS[1]) == 0) then  " +
            "    redis.call('hincrby', KEYS[1], ARGV[2], 1);  " +
            "    redis.call('pexpire', KEYS[1], ARGV[1]);  " +
            "    return true;  " +
            "    end;  " +
            "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
            "    redis.call('hincrby', KEYS[1], ARGV[2], 1);  " +
            "    redis.call('pexpire', KEYS[1], ARGV[1]);  " +
            "    return true;  " +
            "    end;  " +
            "return false;";

    /**
     * 释放锁
     *
     * key 不存在
     *      返回 false
     *      终止
     * key 的重入次数减一后大于 0
     *      设置传入的过期时间
     *      返回 true
     *      不大于 0
     *      删除 key，释放锁
     *      返回 true
     *      终止
     * 释放锁失败返回 false
     */
    private static final String UNLOCK_LUA = "if (redis.call('hexists', KEYS[1], ARGV[2]) == 0) then " +
            "    return false; " +
            "    end; " +
            "if (tonumber(redis.call('hincrby', KEYS[1], ARGV[2], -1)) > 0) then " +
            "    redis.call('pexpire', KEYS[1], ARGV[1]); " +
            "    return true; " +
            "    else " +
            "    redis.call('del', KEYS[1]); " +
            "    return true; " +
            "    end; " +
            "return false;";


    public static Boolean lock(Integer key) {
        return lock(String.valueOf(key), 30L, TimeUnit.SECONDS);
    }

    public static Boolean lock(Long key) {
        return lock(String.valueOf(key), 30L, TimeUnit.SECONDS);
    }

    public static Boolean lock(String key) {
        return lock(key, 30L, TimeUnit.SECONDS);
    }

    public static Boolean lock(Object key) {
        return lock(JSON.toJSONString(key), 30L, TimeUnit.SECONDS);
    }

    public static Boolean lock(String key, Long time, TimeUnit unit) {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>(LOCK_LUA, Boolean.class);
        String stringTime = String.valueOf(unit.toMillis(time));
        String threadInfo = RANDOM + Thread.currentThread().getId();
        log.info("请求加锁 key：{} stringTime：{} threadInfo：{}", key, stringTime, threadInfo);
        return redisTemplate.execute(redisScript, Collections.singletonList(key), stringTime, threadInfo);
    }

    public static Boolean unLock(Integer key) {
        return unLock(String.valueOf(key));
    }

    public static Boolean unLock(Long key) {
        return unLock(String.valueOf(key));
    }

    public static Boolean unLock(Object key) {
        return unLock(JSON.toJSONString(key));
    }

    public static Boolean unLock(String key) {

        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>(UNLOCK_LUA, Boolean.class);

        String stringTime = String.valueOf(30000);
        String threadInfo = RANDOM + Thread.currentThread().getId();
        log.info("请求释放锁 key：{} stringTime：{} threadInfo：{}", key, stringTime, threadInfo);
        Boolean flag = redisTemplate.execute(redisScript, Collections.singletonList(key), stringTime, threadInfo);
        if (Boolean.FALSE.equals(flag)) {
            throw new ExtendException(0, "兄弟，你这个释放锁的操作很危险啊，他已经释放了或者你在释放别人的锁");
        }
        return flag;
    }


}
