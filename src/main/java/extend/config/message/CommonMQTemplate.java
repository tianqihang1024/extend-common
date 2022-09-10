package extend.config.message;

import com.alibaba.fastjson.JSON;
import extend.config.condition.MessageConfigCondition;
import extend.message.BaseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.context.annotation.Conditional;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 田奇杭
 * @Description
 * @Date 2022/9/3 13:05
 */
@Slf4j
@Component
@Conditional(MessageConfigCondition.class)
public class CommonMQTemplate {

    /**
     * 分隔符，spring定义的格式为 topic + “:” + tag
     */
    private static final String DESTINATION = "%s:%s";

    /**
     * topic标签
     */
    private static final String TAG = "*";

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送延迟消息
     *
     * @param topic   主题
     * @param message 消息体
     * @param <T>     消息泛型
     * @return 发送结果
     */
    public <T extends BaseMessage> SendResult send(String topic, T message) {
        // 注意分隔符
        return send(topic, TAG, message);
    }

    /**
     * 发送消息
     *
     * @param topic   主题
     * @param tag     标签
     * @param message 消息体
     * @param <T>     消息泛型
     * @return 发送结果
     */
    public <T extends BaseMessage> SendResult send(String topic, String tag, T message) {
        String destination = String.format(DESTINATION, topic, tag);
        // 设置业务键，此处根据公共的参数进行处理
        // 更多的其它基础业务处理...
        try {
            Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
            return rocketMQTemplate.syncSend(destination, sendMessage);
        } catch (Exception e) {
            log.info("发送消息失败 destination:{}, message:{}, e:{}", destination, JSON.toJSONString(message), e);
            return null;
        }
    }

    /**
     * 发送延迟消息
     *
     * @param topic      主题
     * @param message    消息体
     * @param delayLevel 延迟级别（1-18）
     * @param <T>
     * @return
     */
    public <T extends BaseMessage> SendResult send(String topic, T message, int delayLevel) {
        return send(topic, TAG, message, delayLevel);
    }

    /**
     * 发送延迟消息
     *
     * @param topic      主题
     * @param tag        标签
     * @param message    消息体
     * @param delayLevel 延迟级别（1-18）
     * @param <T>        消息泛型
     * @return 发送结果
     */
    public <T extends BaseMessage> SendResult send(String topic, String tag, T message, int delayLevel) {
        String destination = String.format(DESTINATION, topic, tag);
        try {
            Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
            return rocketMQTemplate.syncSend(destination, sendMessage, 3000, delayLevel);
        } catch (Exception e) {
            log.info("发送延迟消息失败 destination:{}, message:{}, e:{}", destination, JSON.toJSONString(message), e);
            return null;
        }
    }
}