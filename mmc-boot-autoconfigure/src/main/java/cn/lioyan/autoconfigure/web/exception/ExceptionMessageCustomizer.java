package cn.lioyan.autoconfigure.web.exception;

/**
 * @author cn.lioyan
 * @since 2022/4/21 14:00
 */
public interface ExceptionMessageCustomizer {

    String handle(Throwable t);

}
