package com.yx.dynamic.redis.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.data.dynamic.redis")
public class DynamicRedisPoolProperties implements InitializingBean {

    public static final String REDIS_POOL_DEFAULT_NAME = "default";
    private String primary = REDIS_POOL_DEFAULT_NAME;

    private Map<String, RedisProperties> pools = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {

        if (pools.size() > 1 && StringUtils.isEmpty(primary)) {
            throw new IllegalArgumentException("redis pools primary is empty");
        }

    }
}
