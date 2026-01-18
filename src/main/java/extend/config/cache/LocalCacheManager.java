package extend.config.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class LocalCacheManager {

    /**
     * 每个方法有自己的 Cache 实例（按方法签名隔离）
     */
    private final ConcurrentHashMap<String, Cache<String, Object>> cacheMap = new ConcurrentHashMap<>();

    /**
     * 获取缓存实例
     *
     * @param methodKey     方法名
     * @param expireSeconds 缓存存在的时间
     * @return 缓存实例,从中获取具体case的缓存结果
     */
    public Cache<String, Object> getCache(String methodKey, long expireSeconds) {
        return cacheMap.computeIfAbsent(methodKey, k ->
                Caffeine.newBuilder()
                        .maximumSize(10000)
                        .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                        .build()
        );
    }

    /**
     * 添加缓存
     *
     * @param methodKey     方法名
     * @param paramKey      参数值拼接的结果
     * @param value         需要缓存的结果
     * @param expireSeconds 缓存存在的时间
     */
    public void put(String methodKey, String paramKey, Object value, long expireSeconds) {
        getCache(methodKey, expireSeconds).put(paramKey, value);
    }

    /**
     * 获取缓存
     *
     * @param methodKey     方法名
     * @param paramKey      参数值拼接的结果
     * @param expireSeconds 缓存存在的时间
     * @return 缓存结果
     */
    public Object get(String methodKey, String paramKey, long expireSeconds) {
        return getCache(methodKey, expireSeconds).getIfPresent(paramKey);
    }

    /**
     * 清空所有缓存（所有方法的所有条目）
     */
    public void clearAllCaches() {
        cacheMap.values().forEach(Cache::invalidateAll);
    }

}