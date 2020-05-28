package com.yx.dynamic.redis.factory;

import com.yx.dynamic.redis.autoconfigure.DynamicRedisPoolProperties;
import com.yx.dynamic.redis.toolkit.DynamicRedisPoolContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
public class DynamicRoutingConnectionFactory extends AbstractRoutingConnectionFactory {


    private RedisConnectionFactory primaryLettuceConnectionFactory;

    private DynamicRedisPoolProperties dynamicRedisPoolProperties;

    @Autowired(required = false)
    @Qualifier(DynamicLettuceConnectionFactoryBean.FACTORY_BEAN_PREFIX)
    private Map<String, RedisConnectionFactory> connectionFactoryMap;

    public DynamicRoutingConnectionFactory(DynamicRedisPoolProperties dynamicRedisPoolProperties) {
        this.dynamicRedisPoolProperties = dynamicRedisPoolProperties;

    }

    @Override
    protected RedisConnectionFactory determineDataSource() {
        String poolName = DynamicRedisPoolContextHolder.peek();

        if (StringUtils.isEmpty(poolName)) {

            if (primaryLettuceConnectionFactory == null) {
                findPrimaryConnectionFactory();
            }
            return primaryLettuceConnectionFactory;
        }

        return connectionFactoryMap.get(DynamicLettuceConnectionFactoryBean.FACTORY_BEAN_PREFIX + poolName);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        findPrimaryConnectionFactory();
    }

    private void findPrimaryConnectionFactory() {
        this.dynamicRedisPoolProperties.getPools().forEach((pool, redisProperties) -> {
            if (pool.equals(dynamicRedisPoolProperties.getPrimary())) {
                primaryLettuceConnectionFactory = connectionFactoryMap.get(DynamicLettuceConnectionFactoryBean.FACTORY_BEAN_PREFIX + pool);
            }
        });
        if (primaryLettuceConnectionFactory == null) {
            throw new IllegalArgumentException("primaryLettuceConnectionFactory can not be null");
        }
    }


}
