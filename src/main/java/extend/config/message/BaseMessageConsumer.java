package extend.config.message;

import com.alibaba.fastjson.JSON;
import extend.enums.ExtendExceptionEnum;
import extend.exception.ExtendException;
import extend.message.BaseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;

/**
 * @author 田奇杭
 * @Description
 * @Date 2022/9/3 19:48
 */
@Slf4j
public abstract class BaseMessageConsumer<T extends BaseMessage> implements MessageListenerConcurrently {

    @Resource
    private CommonMQTemplate rocketMqTemplate;

    /**
     * 消息者名称
     *
     * @return 消费者名称
     */
    protected abstract String consumerName();

    /**
     * 消息体转换
     *
     * @param messageExt 待处理消息
     * @return 自定义对象
     */
    protected abstract T messageConversion(MessageExt messageExt);

    /**
     * 消息处理
     *
     * @param message 待处理消息
     */
    protected abstract void handleMessage(T message);

    /**
     * 超过重试次数消息，需要启用isRetry
     *
     * @param message 待处理消息
     */
    protected abstract void overMaxRetryTimesMessage(T message);

    /**
     * 是否过滤消息，例如某些
     *
     * @param message 待处理消息
     * @return true: 本次消息被过滤，false：不过滤
     */
    protected boolean isFilter(T message) {
        return false;
    }

    /**
     * 是否异常时重复发送
     *
     * @return true: 消息重试，false：不重试
     */
    protected abstract boolean isRetry();

    /**
     * 消费异常时是否抛出异常
     *
     * @return true: 抛出异常，false：消费异常(如果没有开启重试则消息会被自动ack)
     */
    protected abstract boolean isThrowException();

    /**
     * 最大重试次数
     *
     * @return 最大重试次数，默认3次
     */
    protected int maxRetryTimes() {
        return 3;
    }

    /**
     * isRetry开启时，重新入队延迟时间
     *
     * @return -1：立即入队重试
     */
    protected int retryDelayLevel() {
        return -1;
    }

    /**
     * 默认msg里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
     * 不要抛异常，如果没有return CONSUME_SUCCESS ，consumer会重新消费该消息，直到return CONSUME_SUCCESS
     *
     * @param list
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        MessageExt messageExt;
        if (CollectionUtils.isEmpty(list) || (messageExt = list.get(0)) == null) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        T t = messageConversion(messageExt);

        // 基础日志记录被父类处理了
        log.info("[{}]消费者收到消息[{}]", consumerName(), JSON.toJSONString(t));
        if (isFilter(t)) {
            log.info("消息不满足消费条件，已过滤");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        // 超过最大重试次数时调用子类方法处理
        if (t.getRetryTimes() > maxRetryTimes()) {
            overMaxRetryTimesMessage(t);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        try {
            long start = Instant.now().toEpochMilli();
            handleMessage(t);
            long end = Instant.now().toEpochMilli();
            log.info("消息消费成功，耗时[{}ms]", (end - start));
        } catch (Exception e) {
            log.error("消息处理失败 消息体:{} e:{}", JSON.toJSONString(t), e);
            // 是捕获异常还是抛出，由子类决定
            if (isThrowException()) {
                throw new ExtendException(e);
            }
            if (isRetry()) {
                // 获取子类RocketMQMessageListener注解拿到topic和tag
                t.setSource(t.getSource() + "消息重试");
                t.setRetryTimes(t.getRetryTimes() + 1);
                SendResult sendResult;
                try {
                    // 如果消息发送不成功，则再次重新发送，如果发送异常则抛出由MQ再次处理(异常时不走延迟消息)
                    // 此处捕获之后，相当于此条消息被消息完成然后重新发送新的消息
                    sendResult = rocketMqTemplate.send(messageExt.getTopic(), t, retryDelayLevel());
                } catch (Exception ex) {
                    throw new ExtendException(ex);
                }
                // 发送失败的处理就是不进行ACK，由RocketMQ重试
                if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                    throw new ExtendException(ExtendExceptionEnum.RETRY_MESSAGE_SENDING_EXCEPTION.getCode(), ExtendExceptionEnum.RETRY_MESSAGE_SENDING_EXCEPTION.getMsg());
                }
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
