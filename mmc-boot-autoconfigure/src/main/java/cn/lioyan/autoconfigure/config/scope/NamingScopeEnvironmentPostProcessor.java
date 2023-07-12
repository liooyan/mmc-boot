package cn.lioyan.autoconfigure.config.scope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * {@link NamingScopeEnvironmentPostProcessor}
 *
 * @author com.lioyan
 * @since  2023/7/12  9:55
 */
public class NamingScopeEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            // 加载自定义配置文件
            String customConfigPath = "/path/to/custom.config";
            byte[] customConfigBytes = Files.readAllBytes(Paths.get(customConfigPath));
            String customConfigContent = new String(customConfigBytes);
            environment.getPropertySources().addLast(new CustomPropertySource(customConfigContent));
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
    }

    private static class CustomPropertySource extends PropertySource<String>
    {

        public static final String PROPERTY_SOURCE_NAME = "custom";

        private final String customConfigContent;

        public CustomPropertySource(String customConfigContent) {
            super(PROPERTY_SOURCE_NAME, customConfigContent);
            this.customConfigContent = customConfigContent;
        }

        @Override
        public Object getProperty(String name) {
            // 实现获取特定配置项的逻辑
            return null;
        }
    }
}
