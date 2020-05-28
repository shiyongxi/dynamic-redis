package com.yx.dynamic.redis.aop.annotation;


import com.yx.dynamic.redis.annotation.RedisPool;
import lombok.NonNull;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * 动态数据源AOP织入
 *
 */
public class DynamicRedisPoolAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private Advice advice;

    private Pointcut pointcut;

    public DynamicRedisPoolAnnotationAdvisor(@NonNull DynamicRedisPoolAnnotationInterceptor dynamicRedisPoolAnnotationInterceptor) {
        this.advice = dynamicRedisPoolAnnotationInterceptor;
        this.pointcut = buildPointcut();
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    private Pointcut buildPointcut() {
        Pointcut cpc = new AnnotationMatchingPointcut(RedisPool.class, true);
        Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(RedisPool.class);
        return new ComposablePointcut(cpc).union(mpc);
    }
}
