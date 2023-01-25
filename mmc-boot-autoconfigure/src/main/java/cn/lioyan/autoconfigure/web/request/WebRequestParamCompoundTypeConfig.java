package cn.lioyan.autoconfigure.web.request;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * {@link WebRequestParamCompoundTypeConfig}
 *
 * @author com.lioyan
 * @since 2022/10/8  19:07
 */
@Configuration
public class WebRequestParamCompoundTypeConfig implements WebMvcConfigurer
{
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestParamCompoundTypeHandlerMethodArgumentResolver());
    }
}
