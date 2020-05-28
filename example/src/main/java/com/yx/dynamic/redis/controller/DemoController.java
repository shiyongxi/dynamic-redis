package com.yx.dynamic.redis.controller;

import com.yx.dynamic.redis.annotation.RedisPool;
import com.yx.dynamic.redis.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: shiyongxi
 * @Date: 2020-05-28 17:02
 * @Description: DemoController
 */
@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DemoService demoService;

    @RequestMapping("/set")
    @RedisPool("one")
    public String set(@RequestParam("key") String key, @RequestParam("value") String value) {
        stringRedisTemplate.opsForValue().set(key, value);

        return stringRedisTemplate.opsForValue().get(key);
    }

    @RequestMapping("/get")
    public String get(@RequestParam("key") String key) {
        return demoService.get(key);
    }
}
