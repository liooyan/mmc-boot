package cn.lioyan.autoconfigure.data.redis;

import cn.lioyan.autoconfigure.config.scope.ScopeFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * {@link RedisScopeFactory}
 *
 * @author com.lioyan
 * @since 2023/7/12  19:43
 */
public class RedisScopeFactory implements ScopeFactory<RedisConnectionFactory, RedisStandaloneConfiguration> {
    @Override
    public String getConfigBasePath() {
        return "spring.redis";
    }

    @Override
    public Class<RedisStandaloneConfiguration> getConfigClass() {
        return RedisStandaloneConfiguration.class;
    }

    @Override
    public Class<RedisConnectionFactory> getBeanClass() {
        return RedisConnectionFactory.class;
    }

    @Override
    public RedisConnectionFactory getBean(RedisStandaloneConfiguration redisStandaloneConfiguration) {
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
}
