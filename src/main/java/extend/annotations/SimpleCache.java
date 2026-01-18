package extend.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleCache {

    /**
     * 缓存过期时间（秒）
     */
    long expireAfterWrite() default 300;

}