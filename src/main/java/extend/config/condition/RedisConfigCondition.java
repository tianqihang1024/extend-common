package extend.config.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author 田奇杭
 * @Description
 * @Date 2022/9/10 11:40
 */
@Component
public class RedisConfigCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        String host = environment.getProperty("spring.redis.host");
        return !StringUtils.isEmpty(host);
    }
}
