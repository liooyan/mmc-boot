package cn.lioyan.autoconfigure.data.es;

import cn.lioyan.core.exception.ServiceException;
import cn.lioyan.core.model.base.page.PageData;
import cn.lioyan.core.util.NullUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@link RequestSearchHitsAdvice}
 * {@link  SearchHits}
 * 针对es的查询结果，统计将其封装为{@link PageData}对象
 *
 * @author cn.lioyan
 * @since 2022/4/25 17:23
 */
@ControllerAdvice
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SearchHits.class)
@Order(Integer.MIN_VALUE)
public class RequestSearchHitsAdvice implements ResponseBodyAdvice<Object> {

    private final Logger log = LoggerFactory.getLogger(RequestSearchHitsAdvice.class);
    private Map<String, Field> map = new ConcurrentHashMap<>();


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (!(body instanceof SearchHits)) {
            return body;
        }
        SearchHits<Object> searchHits = (SearchHits<Object>) body;
        List<Object> collect = searchHits.stream().map(s -> {
            Object content = s.getContent();
            Class<?> clazz = content.getClass();
            String packageName =clazz.getName();
            Map<String, List<String>> highlightFields = s.getHighlightFields();
            for (Map.Entry<String, List<String>> stringListEntry : highlightFields.entrySet()) {
                String key = stringListEntry.getKey();
                List<String> value = stringListEntry.getValue();

                Field field = map.get(packageName + "." + key);
                try {
                    String valueString = "";
                    if (NullUtil.notNull(value)) {
                        valueString = value.stream().reduce((a, b) -> a + "," + b).orElse("");
                    }

                    if (field != null) {
                        field.set(content, valueString);
                    } else {
                        Field declaredField = clazz.getDeclaredField(key);
                        declaredField.setAccessible(true);//去除权限
                        declaredField.set(content, valueString);
                        map.put(packageName + "." + key, declaredField);
                    }
                } catch (NoSuchFieldException e) {
                    log.debug("高亮获取字段异常，{}", packageName + "." + key);
                } catch (Exception e) {
                    throw ServiceException.newInstance("高亮转换异常");
                }
            }

            return content;
        }).collect(Collectors.toList());

        return new PageData(collect, searchHits.getTotalHits());
    }
}
