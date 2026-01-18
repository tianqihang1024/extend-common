package extend.enums;

public enum CacheType {
    /**
     * 仅本地缓存（Caffeine）
     */
    LOCAL_ONLY,

    /**
     * 仅远程缓存（Redis）
     */
    REDIS_ONLY,

    /**
     * 两级缓存（本地 + Redis）—— 默认
     */
    BOTH
}