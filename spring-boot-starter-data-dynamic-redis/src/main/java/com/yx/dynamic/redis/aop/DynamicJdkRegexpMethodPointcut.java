package com.yx.dynamic.redis.aop;


import org.springframework.aop.support.JdkRegexpMethodPointcut;

import java.util.Map;


public class DynamicJdkRegexpMethodPointcut extends JdkRegexpMethodPointcut {
    private Map<String, String> matchesCache;

    private String ds;

    public DynamicJdkRegexpMethodPointcut(String pattern, String ds, Map<String, String> matchesCache) {
        this.ds = ds;
        this.matchesCache = matchesCache;
        setPattern(pattern);
    }

    @Override
    protected boolean matches(String pattern, int patternIndex) {
        boolean matches = super.matches(pattern, patternIndex);
        if (matches) {
            matchesCache.put(pattern, ds);
        }
        return matches;
    }
}
