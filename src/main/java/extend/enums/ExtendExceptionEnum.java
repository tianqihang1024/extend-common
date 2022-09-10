package extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 田奇杭
 * @Description 通用异常
 * @Date 2022/9/3 13:05
 */
@Getter
@AllArgsConstructor
public enum ExtendExceptionEnum {

    /**
     * 系统异常
     */
    SYSTEM_EXCEPTION(100000, "系统异常"),

    /**
     * 消息处理失败异常
     */
    MESSAGE_PROCESSING_EXCEPTION(100001, "消息处理失败异常"),

    /**
     * 重试消息发送异常
     */
    RETRY_MESSAGE_SENDING_EXCEPTION(100002, "重试消息发送异常"),

    ;

    /**
     * 异常编码
     */
    private final int code;

    /**
     * 异常信息
     */
    private final String msg;

}
