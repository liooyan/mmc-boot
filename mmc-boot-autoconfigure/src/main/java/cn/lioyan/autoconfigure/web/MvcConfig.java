package cn.lioyan.autoconfigure.web;


import cn.lioyan.core.model.base.page.PageData;
import cn.lioyan.core.model.result.RestResp;
import cn.lioyan.core.util.JacksonUtils;
import cn.lioyan.autoconfigure.config.ConfigProperties;
import cn.lioyan.autoconfigure.web.exception.GlobalExceptionHandler;
import cn.lioyan.autoconfigure.web.exception.error.DefaultErrorView;
import cn.lioyan.autoconfigure.web.exception.error.ErrorPageController;
import cn.lioyan.autoconfigure.web.response.ExcludeRestRespResponse;
import cn.lioyan.autoconfigure.web.response.FileInfoResponseBodyReturnValueHandler;
import cn.lioyan.autoconfigure.web.response.RestRespResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link MvcConfig}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({DispatcherServlet.class})
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MvcConfig {


    @Configuration(proxyBeanMethods = false)
    @Import({GlobalExceptionHandler.class})
    @EnableConfigurationProperties(ServerProperties.class)
    protected static class SpringMvcExceptionHandler {
        @Bean
        @ConditionalOnMissingBean(ErrorController.class)
        public ErrorPageController errorController(ErrorAttributes errorAttributes, ServerProperties serverProperties, ObjectProvider<ErrorViewResolver> errorViewResolvers) {
            return new ErrorPageController(errorAttributes, serverProperties.getError(), errorViewResolvers.orderedStream().collect(Collectors.toList()));
        }

        @Bean(name = "error")
        @ConditionalOnProperty(prefix = "server.error.whitelabel", name = "enabled", matchIfMissing = true)
        @ConditionalOnMissingBean(name = "error")
        public View defaultErrorView(ServerProperties serverProperties) {
            return new DefaultErrorView(serverProperties);
        }

        @Bean
        @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
        public DefaultErrorAttributes errorAttributes() {
            return new DefaultErrorAttributes();
        }
    }

    @Bean
    public FileInfoResponseBodyReturnValueHandler secHandlerConfig(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        FileInfoResponseBodyReturnValueHandler fileInfoResponseBodyReturnValueHandler = new FileInfoResponseBodyReturnValueHandler();
        ArrayList<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
        returnValueHandlers.add(fileInfoResponseBodyReturnValueHandler);
        final List<HandlerMethodReturnValueHandler> oldReturnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        returnValueHandlers.addAll(oldReturnValueHandlers);
        requestMappingHandlerAdapter.setReturnValueHandlers(returnValueHandlers);

        return fileInfoResponseBodyReturnValueHandler;
    }

    /**
     * 对于所有mvc的接口，设置统一的返回对象 {@link  RestResp}
     */
    @ControllerAdvice
    @Configuration(proxyBeanMethods = false)
    @ConfigurationProperties(prefix = "sec.mvc.response.restresp")
    public class RestRespDataResponseBodyAdvice implements ResponseBodyAdvice<Object> {

        private String level;

        private String bashPath;

        public RestRespDataResponseBodyAdvice(Environment environment) {
            this.bashPath = environment.getProperty(ConfigProperties.APP_BASE_PACKAGE);
        }


        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            if ("none".equals(level)) {
                return false;
            }
            if (returnType.getParameterType() == RestResp.class) {
                return false;
            }
            ExcludeRestRespResponse excludeRestRespResponse = returnType.getMethodAnnotation(ExcludeRestRespResponse.class);
            if (excludeRestRespResponse != null) {
                return false;
            }
            if ("all".equals(level)) {
                return true;
            }
            if ("annotation".equals(level)) {
                RestRespResponse methodAnnotation = returnType.getMethodAnnotation(RestRespResponse.class);
                if (methodAnnotation != null) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            String canonicalName = returnType.getContainingClass().getCanonicalName();
            if (!canonicalName.startsWith(bashPath)) {
                return body;
            }

            if (body instanceof PageData) {
                return new RestResp(((PageData<?>) body).getData(), ((PageData<?>) body).getCount());
            } else if(body instanceof RestResp){
                return body;

            }else if(body instanceof String){
                return JacksonUtils.toJson(new RestResp(body));
            }else {
                return new RestResp(body);
            }

        }


        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

    }

}
