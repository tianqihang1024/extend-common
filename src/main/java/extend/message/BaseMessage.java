package extend.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author 田奇杭
 * @description 基础消息实体
 * @date 2022/9/3 10:46
 * 基础消息实体，包含一些基础的消息信息
 * 所有消息对象必须继承此对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseMessage {

    /**
     * 业务键，用于RocketMQ控制台查看消费情况
     */
    protected String key;

    /**
     * 发送消息来源，用于排查问题
     */
    protected String source = "";

    /**
     * 发送时间
     */
    protected String sendTime = LocalDateTime.now().toString();

    /**
     * 跟踪id，用于slf4j等日志记录跟踪id，方便查询业务链
     */
    protected String traceId = UUID.randomUUID().toString();

    /**
     * 重试次数，用于判断重试次数，超过重试次数发送异常警告
     */
    protected Integer retryTimes = 0;

}
