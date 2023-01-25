package cn.lioyan.core.exception;

/**
 * 异常父类，所有模块的异常均继承于此类
 *
 * {@link BaseException}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class BaseException extends RuntimeException {

    private Integer code = ExceptionKeys.DEFAULT_ERROR_CODE;

    protected BaseException(Integer code) {
        this(code, ErrorMsg.getErrorMsg(code));
    }

    protected BaseException(String msg) {
        super(msg);
    }

    protected BaseException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    protected BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BaseException(Throwable cause) {
        super(cause);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
