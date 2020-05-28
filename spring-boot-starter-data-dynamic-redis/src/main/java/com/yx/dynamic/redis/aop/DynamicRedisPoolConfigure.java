package com.yx.dynamic.redis.aop;


import com.yx.dynamic.redis.matcher.ExpressionMatcher;
import com.yx.dynamic.redis.matcher.Matcher;
import com.yx.dynamic.redis.matcher.RegexMatcher;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * 基于多种策略的自动切换数据源
 */
public class DynamicRedisPoolConfigure {

    @Getter
    private List<Matcher> matchers = new LinkedList<>();

    private DynamicRedisPoolConfigure() {
    }

    public static DynamicRedisPoolConfigure config() {
        return new DynamicRedisPoolConfigure();
    }

    public DynamicRedisPoolConfigure regexMatchers(String pattern, String ds) {
        matchers.add(new RegexMatcher(pattern, ds));
        return this;
    }

    public DynamicRedisPoolConfigure expressionMatchers(String expression, String ds) {
        matchers.add(new ExpressionMatcher(expression, ds));
        return this;
    }

}
