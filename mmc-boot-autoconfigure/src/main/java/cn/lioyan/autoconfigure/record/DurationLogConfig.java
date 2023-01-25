package cn.lioyan.autoconfigure.record;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link DurationLogConfig}
 *
 * @author cn.lioyan
 * @since 2022/4/14 14:23
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class DurationLogConfig {

    @Bean
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    public DurationLogAop durationAop() {
        return new DurationLogAop();
    }

}
