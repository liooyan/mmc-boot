package cn.lioyan.autoconfigure.util;

import cn.lioyan.core.util.NullUtil;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.*;


/**
 * {@link SpringPropertyUtil}
 * 对于spring 配置获取的一些方式
 * @author com.lioyan
 * @since  2023/7/12  9:52
 */
public class SpringPropertyUtil
{

    public static List<String> getPropertyInAllSource(Environment environment, String propertyName) {
        Set<String> valuseSet = new HashSet<>();
        if (environment instanceof StandardServletEnvironment) {
            MutablePropertySources propertySources = ((StandardServletEnvironment) environment).getPropertySources();
            for (PropertySource<?> propertySource : propertySources) {

               Object valuse = propertySource.getProperty(propertyName);
                if (NullUtil.notNull(valuse)) {
                    //按照，分割
                    String[] split = valuse.toString().split(",");
                    valuseSet.addAll(Arrays.asList(split));
                }
            }
        }
        return new ArrayList<>(valuseSet);
    }

}
