package com.yx.dynamic.redis.factory;

import io.lettuce.core.resource.ClientResources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


@Qualifier(DynamicLettuceConnectionFactoryBean.FACTORY_BEAN_PREFIX)
@Slf4j
public class DynamicLettuceConnectionFactoryBean extends AbstractFactoryBean<LettuceConnectionFactory> implements BeanNameAware {

    public static final String FACTORY_BEAN_PREFIX = "internal-dynamicLettuceConnectionFactoryBean-";

    private PredictLettuceConnectionConfiguration predictLettuceConnectionConfiguration;
    private ClientResources clientResources;

    private String poolName;

    public DynamicLettuceConnectionFactoryBean(PredictLettuceConnectionConfiguration predictLettuceConnectionConfiguration,
                                               ClientResources clientResources) {
        this.predictLettuceConnectionConfiguration = predictLettuceConnectionConfiguration;

        this.clientResources = clientResources;
    }

    @Override
    public void setBeanName(String name) {
        this.poolName = name;
    }

    @Override
    public Class<?> getObjectType() {
        return LettuceConnectionFactory.class;
    }

    @Override
    protected LettuceConnectionFactory createInstance() throws Exception {
        LettuceConnectionFactory lettuceConnectionFactory = predictLettuceConnectionConfiguration.redisConnectionFactory(clientResources);
        lettuceConnectionFactory.afterPropertiesSet();
        log.info("redis pool:{} afterPropertiesSet", poolName);
        return lettuceConnectionFactory;
    }


    @Override
    protected void destroyInstance(LettuceConnectionFactory instance) throws Exception {
        log.info("redis pool:{} destroy", poolName);
        instance.destroy();
    }
}
