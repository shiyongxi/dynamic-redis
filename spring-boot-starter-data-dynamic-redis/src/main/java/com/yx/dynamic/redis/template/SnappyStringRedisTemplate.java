package com.yx.dynamic.redis.template;

import com.yx.dynamic.redis.codec.SnappyStringRedisSerializer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public class SnappyStringRedisTemplate extends StringRedisTemplate {

    public SnappyStringRedisTemplate() {
        setKeySerializer(RedisSerializer.string());
        setValueSerializer(new SnappyStringRedisSerializer());
        setHashKeySerializer(RedisSerializer.string());
        setHashValueSerializer(new SnappyStringRedisSerializer());
    }

}
