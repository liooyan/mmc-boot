package cn.lioyan.quart.admin;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * {@link ImportQuartAdmin}
 *
 * @author com.lioyan
 */
@PropertySource(value = {
        "classpath:xxl.properties",
}, encoding = "utf-8")
@ComponentScan(basePackages  = {"com.xxl.job.admin","cn.lioyan.quart.admin.controller"})
@Configuration(proxyBeanMethods = false)
public class ImportQuartAdmin  {



}
