package com.yx.dynamic.redis.aop.annotation;


import com.yx.dynamic.redis.annotation.RedisPool;
import com.yx.dynamic.redis.aop.DynamicRedisPoolClassResolver;
import com.yx.dynamic.redis.processor.RedisPoolProcessor;
import com.yx.dynamic.redis.toolkit.DynamicRedisPoolContextHolder;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * 动态数据源AOP核心拦截器
 */
public class DynamicRedisPoolAnnotationInterceptor implements MethodInterceptor {

    /**
     * SPEL参数标识
     */
    private static final String DYNAMIC_PREFIX = "#";

    @Setter
    private RedisPoolProcessor redisPoolProcessor;

    private DynamicRedisPoolClassResolver dynamicRedisPoolClassResolver = new DynamicRedisPoolClassResolver();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            DynamicRedisPoolContextHolder.push(determineDatasource(invocation));
            return invocation.proceed();
        } finally {
            DynamicRedisPoolContextHolder.poll();
        }
    }

    private String determineDatasource(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Class<?> targetClass = dynamicRedisPoolClassResolver.targetClass(invocation);
        //优先获取调用方法上的注解，其次是父类方法的注解， 然后是目标类上的注解,最后是父类上的注解
        //总之是方法上的注解优先于类上的注解
        RedisPool ds = AnnotationUtils.findAnnotation(method, RedisPool.class);
        if (ds == null) {
            ds = AnnotationUtils.findAnnotation(targetClass, RedisPool.class);
        }
        if (ds == null) {
            return null;
        }
        String key = ds.value();
        return (!key.isEmpty() && key.startsWith(DYNAMIC_PREFIX)) ? redisPoolProcessor.determineDatasource(invocation, key) : key;
    }
}
