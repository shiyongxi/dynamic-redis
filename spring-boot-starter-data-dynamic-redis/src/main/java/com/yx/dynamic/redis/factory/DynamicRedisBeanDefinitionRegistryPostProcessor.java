package com.yx.dynamic.redis.factory;

import com.yx.dynamic.redis.autoconfigure.DynamicRedisPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;

@Slf4j
public class DynamicRedisBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, PriorityOrdered {


    private Binder binder;


    @Override
    public void setEnvironment(Environment environment) {
        binder = Binder.get(environment);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        DynamicRedisPoolProperties dynamicRedisPoolProperties;
        try {
            dynamicRedisPoolProperties = binder.bind("spring.data.dynamic.redis", DynamicRedisPoolProperties.class).get();
        } catch (Exception e) {
            log.warn("there is no dynamic redis config, skip regisiter bean");
            return;
        }
        log.info("begin register dynamic redis pool");
        dynamicRedisPoolProperties.getPools().forEach((pool, redisProperties) -> {




            //每个pool单独注册一个LettuceConnectionConfiguration
            AbstractBeanDefinition predictLettuceConnectionConfigurationBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(PredictLettuceConnectionConfiguration.class)
                    .addConstructorArgValue(redisProperties)
                    .getRawBeanDefinition();
            registry.registerBeanDefinition(PredictLettuceConnectionConfiguration.FACTORY_BEAN_PREFIX + pool, predictLettuceConnectionConfigurationBeanDefinition);


            //每个pool单独注册一个ClientResources
            AbstractBeanDefinition clientResourcesBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ClientResourcesFactoryBean.class)
                    .getRawBeanDefinition();
            registry.registerBeanDefinition(ClientResourcesFactoryBean.FACTORY_BEAN_PREFIX + pool, clientResourcesBeanDefinition);

            AbstractBeanDefinition connectionFactoryBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(DynamicLettuceConnectionFactoryBean.class)
                    //独立LettuceConnectionConfiguration bean引用
                    .addConstructorArgReference(PredictLettuceConnectionConfiguration.FACTORY_BEAN_PREFIX + pool)
                    //独立ClientResources bean引用
                    .addConstructorArgReference(ClientResourcesFactoryBean.FACTORY_BEAN_PREFIX + pool)
                    .getRawBeanDefinition();
            registry.registerBeanDefinition(DynamicLettuceConnectionFactoryBean.FACTORY_BEAN_PREFIX + pool, connectionFactoryBeanDefinition);

        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
