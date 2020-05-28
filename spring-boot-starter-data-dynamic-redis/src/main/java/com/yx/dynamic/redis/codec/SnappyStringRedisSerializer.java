package com.yx.dynamic.redis.codec;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import org.xerial.snappy.Snappy;

@Slf4j
public class SnappyStringRedisSerializer extends StringRedisSerializer {

    @Override
    public byte[] serialize(String s) throws SerializationException {
        try {
            return !StringUtils.isEmpty(s) ? Snappy.compress(s) : null;
        } catch (Exception e) {
            log.error("snappy serialize got error", e);
            return super.serialize(s);
        }
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        try {
            if (bytes != null) {
                byte[] uncompress = Snappy.uncompress(bytes);
                return super.deserialize(uncompress);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("snappy deserialize got error", e);
            return super.deserialize(bytes);
        }
    }
}
