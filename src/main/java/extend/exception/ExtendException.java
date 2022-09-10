package extend.exception;

/**
 * @author 田奇杭
 * @Description 扩展项目通用异常类
 * @Date 2022/8/28 19:56
 */
public class ExtendException extends RuntimeException{

    private final int code;

    public ExtendException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public ExtendException(Throwable throwable) {
        super(throwable);
        this.code = 0;
    }

    public int getCode() {
        return code;
    }
}
