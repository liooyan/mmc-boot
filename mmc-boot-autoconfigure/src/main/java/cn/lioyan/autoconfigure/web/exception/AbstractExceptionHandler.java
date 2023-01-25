package cn.lioyan.autoconfigure.web.exception;

import cn.lioyan.core.exception.ErrorMsg;
import cn.lioyan.core.exception.ExceptionKeys;
import cn.lioyan.core.model.result.RestResp;
import cn.lioyan.autoconfigure.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author cn.lioyan
 * @since 2022/4/21 14:00
 */
public abstract class AbstractExceptionHandler implements EnvironmentAware
{

    private final Logger log = LoggerFactory.getLogger(AbstractExceptionHandler.class);
    private static final int DEFAULT_ERROR_CODE = ExceptionKeys.DEFAULT_ERROR_CODE;

    private boolean setValidatorResult;
    private boolean removeFrameworkStack;

    private boolean overrideHttpError;

    private List<String> basePackages = new ArrayList<>();

    protected RestResp<Object> buildErrorMessage(Integer code, Object data, Throwable t) {
        return buildErrorMessage(code, null, data, t);
    }


    protected RestResp<Object> buildErrorMessage(Integer code, String msg, Object data, Throwable t) {
        if (code == null) {
            code = DEFAULT_ERROR_CODE;
        }
        if (removeFrameworkStack) {//移除异常栈中非业务应用包下的栈信息
            dealCurrentStackTraceElement(t);
        }
        logError(t);//打印异常栈
        if (ObjectUtils.isEmpty(msg)) {//这里的消息可能是重写后的

            msg = ErrorMsg.getErrorMsg(code);//1.take msg from code
            if (ObjectUtils.isEmpty(msg)) {
                msg = t.getMessage();//2.take msg from exception
                if (ObjectUtils.isEmpty(msg) && code != DEFAULT_ERROR_CODE) {
                    log.warn("please set code = {} exception message", code);//3.log no exception message
                }
            }
        }
        RestResp<Object> resp = RestResp.error(code, msg);
        if (data != null && setValidatorResult) {//设置参数校验具体错误数据信息
            resp.setData(data);
        }
        return resp;
    }

    protected void logError(Throwable t) {
        log.error("The exception information is as follows", t);
    }

    private void dealCurrentStackTraceElement(Throwable exception)
    {

        exception.setStackTrace(Arrays.stream(exception.getStackTrace()).filter(s -> {
            for (String basePackage : basePackages)
            {
                if (s.getClassName().contains(basePackage))
                {
                    return true;
                }
            }
            return false;
        }).toArray(StackTraceElement[]::new));
    }

    @Override
    public void setEnvironment(Environment environment)
    {
        this.removeFrameworkStack = environment.getProperty("framework.stack.remove.enable",
            Boolean.class, true);
        this.setValidatorResult = environment.getProperty("validator.result.return.enable",
            Boolean.class, true);
        this.overrideHttpError = environment.getProperty("http.error.override", Boolean.class,
            true);
        basePackages.add(
            environment.getProperty(ConfigProperties.APP_BASE_PACKAGE, String.class, ""));
        basePackages.add("cn.lioyan");
    }

    int mappingCode(ServletException exception) throws ServletException {
        if (overrideHttpError) {
            int code = ExceptionKeys.HTTP_ERROR_500;
            if (exception instanceof NoHandlerFoundException) {
                code = ExceptionKeys.HTTP_ERROR_404;
            } else if (exception instanceof HttpRequestMethodNotSupportedException) {
                code = ExceptionKeys.HTTP_ERROR_405;
            } else if (exception instanceof HttpMediaTypeException) {
                code = ExceptionKeys.HTTP_ERROR_406;
            } else if (exception instanceof UnavailableException) {
                code = ExceptionKeys.HTTP_ERROR_503;
            }
            return code;
        }
        throw exception;
    }

}
