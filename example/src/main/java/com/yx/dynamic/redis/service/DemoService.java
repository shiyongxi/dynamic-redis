package com.yx.dynamic.redis.service;

import com.yx.dynamic.redis.annotation.RedisPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Auther: shiyongxi
 * @Date: 2020-05-28 17:46
 * @Description: DemoService
 */
@Service
@RedisPool("two")
public class DemoService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RedisPool("two")
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}
