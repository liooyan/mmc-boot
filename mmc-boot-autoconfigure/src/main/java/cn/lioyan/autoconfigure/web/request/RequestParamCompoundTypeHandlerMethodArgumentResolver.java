package cn.lioyan.autoconfigure.web.request;

import cn.lioyan.core.util.BaseTypeUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * {@link RequestParamCompoundTypeHandlerMethodArgumentResolver}
 *
 * @author com.lioyan
 * @since 2022/10/8  18:49
 */
@Component
public class RequestParamCompoundTypeHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    Map<Class, Field[]> resolveClassField = new HashMap<>();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        RequestParamCompoundType parameterAnnotation = parameter.getParameterAnnotation(
                RequestParamCompoundType.class);
        if (parameterAnnotation != null) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {
        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        Class<?> parameterType = parameter.getParameterType();

        Object object = parameterType.newInstance();

        Field[] fields = null;
        if (resolveClassField.containsKey(parameterType)) {
            fields = resolveClassField.get(parameterType);
        } else {
            synchronized (this) {
                fields = parameterType.getDeclaredFields();
                resolveClassField.put(parameterType, fields);
            }
        }

        for (Field field : fields) {
            String name = field.getName();
            String[] strings = parameterMap.get(name);
            if (strings != null && strings.length > 0 && strings[0] != null) {
                Class<?> type = field.getType();
                field.setAccessible(true);
                field.set(object, BaseTypeUtil.parsing(strings[0], type));
            }
        }
        return object;
    }
}
