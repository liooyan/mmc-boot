package cn.lioyan.autoconfigure.web.exception;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cn.lioyan
 * @since 2022/4/21 14:00
 */
public interface GlobalExceptionViewResolver {

    boolean support(HttpServletRequest request);

    ModelAndView view(HttpServletRequest request, Throwable exception);

}
