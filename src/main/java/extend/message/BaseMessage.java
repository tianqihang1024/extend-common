package extend.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author 田奇杭
 * @Description 基础消息实体
 * @Date 2022/9/3 10:46
 * 基础消息实体，包含一些基础的消息信息
 * 所有消息对象必须继承此对象
 */
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

    public BaseMessage() {
    }

    public BaseMessage(String key, String source) {
        this.key = key;
        this.source = source;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseMessage that = (BaseMessage) o;

        if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) return false;
        if (getSource() != null ? !getSource().equals(that.getSource()) : that.getSource() != null) return false;
        if (getSendTime() != null ? !getSendTime().equals(that.getSendTime()) : that.getSendTime() != null)
            return false;
        if (getTraceId() != null ? !getTraceId().equals(that.getTraceId()) : that.getTraceId() != null) return false;
        return getRetryTimes() != null ? getRetryTimes().equals(that.getRetryTimes()) : that.getRetryTimes() == null;
    }

    @Override
    public int hashCode() {
        int result = getKey() != null ? getKey().hashCode() : 0;
        result = 31 * result + (getSource() != null ? getSource().hashCode() : 0);
        result = 31 * result + (getSendTime() != null ? getSendTime().hashCode() : 0);
        result = 31 * result + (getTraceId() != null ? getTraceId().hashCode() : 0);
        result = 31 * result + (getRetryTimes() != null ? getRetryTimes().hashCode() : 0);
        return result;
    }
}
