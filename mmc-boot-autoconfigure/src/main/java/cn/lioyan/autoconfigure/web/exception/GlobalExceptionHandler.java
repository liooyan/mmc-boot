package cn.lioyan.autoconfigure.web.exception;

import cn.lioyan.core.exception.BaseException;
import cn.lioyan.core.exception.ExceptionKeys;
import cn.lioyan.core.model.result.RestResp;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cn.lioyan
 * @since 2022/4/21 14:00
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends AbstractExceptionHandler {

    private final ObjectProvider<ExceptionMessageCustomizer> exceptionHandlers;
    private GlobalExceptionViewResolver viewResolver;

    public GlobalExceptionHandler(ObjectProvider<ExceptionMessageCustomizer> exceptionHandlers) {
        this.exceptionHandlers = exceptionHandlers;
    }


    @ExceptionHandler(Throwable.class)
    public Object handleException(HttpServletRequest request, Throwable exception) throws Throwable {
        if (viewResolver != null && viewResolver.support(request)) {
            logError(exception);
            return viewResolver.view(request, exception);
        }
        return buildErrorData(request, exception);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    public Object validationException(HttpServletRequest request, Throwable exception) {
        if (exception instanceof ValidationException) {
            return handleValidationException((ValidationException) exception);
        }
        if (exception instanceof MethodArgumentNotValidException) {
            return handleValidationException((MethodArgumentNotValidException) exception);
        }
        return null;
    }


    public RestResp<Object> handleValidationException(ValidationException exception) {
        Object data = null;
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            data = cve.getConstraintViolations().stream().map(violation1 ->
                    new ValidationErrorBean(violation1.getMessage(), getViolationPath(violation1), getViolationInvalidValue(violation1.getInvalidValue()))
            ).collect(Collectors.toList());
        }
        return buildErrorMessage(ExceptionKeys.PARAM_PARSE_ERROR, data, exception);
    }

    public RestResp<Object> handleValidationException(MethodArgumentNotValidException exception) {
        final List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
        Object data = allErrors.stream().map(error -> {
                    if (error instanceof FieldError) {
                        String  field = ((FieldError) error).getField();
                        Object  rejectedValue = ((FieldError) error).getRejectedValue();
                        return new ValidationErrorBean(error.getDefaultMessage(), field,rejectedValue);
                    }else {
                        return new ValidationErrorBean(error.getDefaultMessage(), error.getObjectName(),error.getCode());
                    }

                }
        ).collect(Collectors.toList());
        return buildErrorMessage(ExceptionKeys.PARAM_PARSE_ERROR, data, exception);
    }

    private String getViolationInvalidValue(Object invalidValue) {
        if (invalidValue == null) {
            return null;
        } else {
            if (invalidValue.getClass().isArray()) {
                if (invalidValue instanceof Object[]) {
                    return Arrays.toString((Object[]) invalidValue);
                }

                if (invalidValue instanceof boolean[]) {
                    return Arrays.toString((boolean[]) invalidValue);
                }

                if (invalidValue instanceof byte[]) {
                    return Arrays.toString((byte[]) invalidValue);
                }

                if (invalidValue instanceof char[]) {
                    return Arrays.toString((char[]) invalidValue);
                }

                if (invalidValue instanceof double[]) {
                    return Arrays.toString((double[]) invalidValue);
                }

                if (invalidValue instanceof float[]) {
                    return Arrays.toString((float[]) invalidValue);
                }

                if (invalidValue instanceof int[]) {
                    return Arrays.toString((int[]) invalidValue);
                }

                if (invalidValue instanceof long[]) {
                    return Arrays.toString((long[]) invalidValue);
                }

                if (invalidValue instanceof short[]) {
                    return Arrays.toString((short[]) invalidValue);
                }
            }

            return invalidValue.toString();
        }
    }

    private String getViolationPath(ConstraintViolation violation) {
        String rootBeanName = violation.getRootBean().getClass().getSimpleName();
        String propertyPath = violation.getPropertyPath().toString();
        return rootBeanName + (!"".equals(propertyPath) ? '.' + propertyPath : "");
    }


    public RestResp<Object> buildErrorData(HttpServletRequest request, Throwable exception) throws Throwable {
        Integer errorCode = null;
        Object data = null;
        if (exception instanceof BaseException) {
            errorCode = ((BaseException) exception).getCode();
        }

        if (exception instanceof ServletException) {
            errorCode = mappingCode(((ServletException) exception));
        }
        ExceptionMessageCustomizer exceptionHandler = exceptionHandlers.getIfUnique();
        if (Objects.nonNull(exceptionHandler)) {
            return buildErrorMessage(errorCode, exceptionHandler.handle(exception), data, exception);
        }
        return buildErrorMessage(errorCode, data, exception);
    }

    public void setErrorViewResolver(GlobalExceptionViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

}
