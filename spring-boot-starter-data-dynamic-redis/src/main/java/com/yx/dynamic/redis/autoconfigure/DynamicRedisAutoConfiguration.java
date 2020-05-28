package com.yx.dynamic.redis.autoconfigure;

import com.yx.dynamic.redis.aop.DynamicRedisPoolAdvisor;
import com.yx.dynamic.redis.aop.DynamicRedisPoolConfigure;
import com.yx.dynamic.redis.aop.annotation.DynamicRedisPoolAnnotationAdvisor;
import com.yx.dynamic.redis.aop.annotation.DynamicRedisPoolAnnotationInterceptor;
import com.yx.dynamic.redis.factory.DynamicRedisBeanDefinitionRegistryPostProcessor;
import com.yx.dynamic.redis.factory.DynamicRoutingConnectionFactory;
import com.yx.dynamic.redis.processor.RedisPoolHeaderProcessor;
import com.yx.dynamic.redis.processor.RedisPoolProcessor;
import com.yx.dynamic.redis.processor.RedisPoolSessionProcessor;
import com.yx.dynamic.redis.processor.RedisPoolSpelExpressionProcessor;
import com.yx.dynamic.redis.template.SnappyStringRedisTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;

@Configuration
@ComponentScan("com.yx.dynamic.redis")
@AutoConfigureBefore(RedisAutoConfiguration.class)
@EnableConfigurationProperties(DynamicRedisPoolProperties.class)
public class DynamicRedisAutoConfiguration {

    @Bean(name = "redisKeySerializer")
    @ConditionalOnMissingBean(name = "redisKeySerializer")
    public RedisSerializer redisKeySerializer() {
        return StringRedisSerializer.UTF_8;
    }


//    @Bean
//    public DynamicRoutingConnectionFactoryBeanPostProcessor dynamicRoutingConnectionFactoryBeanPostProcessor() {
//        return new DynamicRoutingConnectionFactoryBeanPostProcessor();
//    }

    @Bean
    public DynamicRedisBeanDefinitionRegistryPostProcessor dynamicRedisBeanDefinitionRegistryPostProcessor() {
        return new DynamicRedisBeanDefinitionRegistryPostProcessor();
    }

    @Bean
    @Primary
    public DynamicRoutingConnectionFactory dynamicRoutingConnectionFactory(DynamicRedisPoolProperties dynamicRedisPoolProperties) {
        return new DynamicRoutingConnectionFactory(dynamicRedisPoolProperties);
    }


    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(redisKeySerializer());
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(redisKeySerializer());
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public SnappyStringRedisTemplate snappyStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        SnappyStringRedisTemplate template = new SnappyStringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(redisKeySerializer());
        return template;
    }


    @Bean
    @ConditionalOnMissingBean
    public RedisPoolProcessor redisPoolProcessor() {
        RedisPoolHeaderProcessor headerProcessor = new RedisPoolHeaderProcessor();
        RedisPoolSessionProcessor sessionProcessor = new RedisPoolSessionProcessor();
        RedisPoolSpelExpressionProcessor spelExpressionProcessor = new RedisPoolSpelExpressionProcessor();
        headerProcessor.setNextProcessor(sessionProcessor);
        sessionProcessor.setNextProcessor(spelExpressionProcessor);
        return headerProcessor;
    }

    /**
     * RedisPool 注解方式动态数据源
     *
     * @param redisPoolProcessor
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DynamicRedisPoolAnnotationAdvisor dynamicRedisPoolAnnotationAdvisor(RedisPoolProcessor redisPoolProcessor) {
        DynamicRedisPoolAnnotationInterceptor interceptor = new DynamicRedisPoolAnnotationInterceptor();
        interceptor.setRedisPoolProcessor(redisPoolProcessor);
        DynamicRedisPoolAnnotationAdvisor advisor = new DynamicRedisPoolAnnotationAdvisor(interceptor);
        return advisor;
    }


    /**
     * 动态属性配置方式进行aop拦截切换数据源
     *
     * @param dynamicRedisPoolConfigure
     * @param redisPoolProcessor
     * @return
     */
    @Bean
    @ConditionalOnBean(DynamicRedisPoolConfigure.class)
    public DynamicRedisPoolAdvisor dynamicRedisPoolAdvisor(DynamicRedisPoolConfigure dynamicRedisPoolConfigure, RedisPoolProcessor redisPoolProcessor) {
        DynamicRedisPoolAdvisor advisor = new DynamicRedisPoolAdvisor(dynamicRedisPoolConfigure.getMatchers());
        advisor.setRedisPoolProcessor(redisPoolProcessor);
        return advisor;
    }


    @Bean
    @ConditionalOnBean(MessageListenerAdapter.class)
    public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("predict.memory.sync"));

        return container;
    }


}
