package extend.config.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author 田奇杭
 * @Description 这里的condition没有起作用，因为不引入依赖代码会报错，但是引入了依赖这个condition又一定是true，你就当认识下这个注解吧
 * @Date 2022/9/10 12:27
 */
@Component
public class MessageConfigCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        String producer = environment.getProperty("rocketmq.name-server");
        return !StringUtils.isEmpty(producer);
    }
}
