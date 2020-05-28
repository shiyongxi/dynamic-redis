package com.yx.dynamic.redis.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Auther: shiyongxi
 * @Date: 2020-05-28 15:05
 * @Description: RedisPool
 * 优先获取调用方法上的注解，其次是父类方法的注解， 然后是目标类上的注解,最后是父类上的注解
 * 总之是方法上的注解优先于类上的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited

public @interface RedisPool {

    @AliasFor("name")
    String value() default "";


    @AliasFor("value")
    String name() default "";
}
