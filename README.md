# dynamic-redis


> redis 多数据源动态路由模块。

引入依赖:
```xml
        <dependency>
            <groupId>com.yx</groupId>
            <artifactId>spring-boot-starter-data-dynamic-redis</artifactId>
        </dependency>
```

配置承载类`DynamicRedisPoolProperties`,spring.data.dynamic.redis为前缀,以下模拟一个多数据源配置，有三个数据源，名称分别为one,two,主数据源通过`primary`指定为one
这里demo通过database不一样来区分不同的数据源.
通过@RedisPool来指定数据源，如果没有指定则默认为primary对应的数据源连接池,单连接池的配置和spring-data-redis的 RedisProperties一致。
多数据源模块默认也支持单数据源，只是前缀和配置方式不一样而已.
```yaml
spring:
  data:
    dynamic:
      redis:
        #设置主数据源
        primary: one
        pools:
          #连接池one的配置
          one:
            host: 127.0.0.1
            password:
            database: 1
            timeout: 3s
            lettuce:
              pool:
                max-active: 8
                max-idle: 8
                min-idle: 1
                max-wait: 3s
          #连接池two的配置
          two:
            host: 127.0.0.1
            password:
            database: 2
            timeout: 3s
            lettuce:
              pool:
                max-active: 8
                max-idle: 8
                min-idle: 1
                max-wait: 3s
```

```java
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

```

@RedisPool注解可以打在类上，也可以打在方法上，优先获取调用方法上的注解，其次是父类方法的注解， 然后是目标类上的注解,最后是父类上的注解，总之是方法上的注解优先于类上的注解
以下为一个打在方法上的注解示例
```java
    @RestController
    @RequestMapping("/demo")
    @Slf4j
    public class DemoController {
        @Autowired
        private StringRedisTemplate stringRedisTemplate;
        @Autowired
        private DemoService demoService;
    
        @RequestMapping("/set")
        @RedisPool("two")
        public String set(@RequestParam("key") String key, @RequestParam("value") String value) {
            stringRedisTemplate.opsForValue().set(key, value);
    
            return stringRedisTemplate.opsForValue().get(key);
        }
    
        @RequestMapping("/get")
        public String get(@RequestParam("key") String key) {
            return demoService.get(key);
        }
    }
```