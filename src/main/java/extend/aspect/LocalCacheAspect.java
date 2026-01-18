package extend.aspect;

import extend.annotations.SimpleCache;
import extend.config.cache.LocalCacheManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Order(1) // 确保在事务等切面之前执行
public class LocalCacheAspect {

    @Resource
    private LocalCacheManager cacheManager;

    @Around("@annotation(simpleCache)")
    public Object cacheAround(ProceedingJoinPoint joinPoint, SimpleCache simpleCache) throws Throwable {
        // 1. 构建方法唯一标识：类名 + 方法名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        String methodKey = className + "." + methodName;

        // 2. 构建参数 key：将参数数组转为字符串（简单方案）
        Object[] args = joinPoint.getArgs();
        String paramKey = Arrays.deepToString(args); // 处理多维数组

        // 3. 尝试从缓存获取
        Object cached = cacheManager.get(methodKey, paramKey,  simpleCache.expireAfterWrite());
        if (cached != null) {
            return cached;
        }

        // 4. 缓存未命中，执行原方法
        Object result = joinPoint.proceed();

        // 5. 写入缓存（注意：避免缓存 null，可选）
        if (result != null) {
            cacheManager.put(methodKey, paramKey, result, simpleCache.expireAfterWrite());
        }

        return result;
    }
}