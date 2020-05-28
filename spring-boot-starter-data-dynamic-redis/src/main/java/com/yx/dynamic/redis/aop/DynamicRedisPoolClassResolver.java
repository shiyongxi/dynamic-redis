package com.yx.dynamic.redis.aop;


import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;


@Slf4j
public class DynamicRedisPoolClassResolver {


    public Class<?> targetClass(MethodInvocation invocation) throws IllegalAccessException {

        //获取目标真实类
        Object target = invocation.getThis();
        Class<?> targetClass = target.getClass();
//        return Proxy.isProxyClass(targetClass) ? (Class) mapperInterfaceField.get(Proxy.getInvocationHandler(target)) : targetClass;
        return targetClass;

    }
}
