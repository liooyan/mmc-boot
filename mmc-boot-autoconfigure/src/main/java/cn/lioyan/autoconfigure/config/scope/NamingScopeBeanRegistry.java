package cn.lioyan.autoconfigure.config.scope;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link NamingScopeBeanRegistry}
 *
 * @author com.lioyan
 * @since  2023/7/12  11:26
 */
public class NamingScopeBeanRegistry implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private Map<Class, Map<String, Object>> scopeBeanRegistry = new HashMap<>();
    private Map<Class, Object> defScopeBeanRegistry = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public void registry(Class clazz, String scopeName, Object bean) {
        Map<String, Object> beanMap = scopeBeanRegistry.computeIfAbsent(clazz, k -> new HashMap<>());
        if (scopeName == null) {
            defScopeBeanRegistry.put(clazz, bean);
        } else {
            beanMap.put(scopeName, bean);
        }
    }

    //获取
    public Object getBean(Class clazz, String scopeName) {
        if (scopeName == null) {
            return defScopeBeanRegistry.get(clazz);
        }
        Map<String, Object> beanMap = scopeBeanRegistry.computeIfAbsent(clazz, k -> new HashMap<>());
        Object o = beanMap.get(scopeName);
        if (o == null) {
            return defScopeBeanRegistry.get(clazz);
        }
        return o;
    }

    public Object getBeanDef(Class clazz) {
        return defScopeBeanRegistry.get(clazz);
    }
}
