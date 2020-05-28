package com.yx.dynamic.redis.matcher;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Auther: shiyongxi
 * @Date: 2020-05-28 15:05
 * @Description: RegexMatcher
 */
@AllArgsConstructor
@Data
public class RegexMatcher implements Matcher {
    private String pattern;

    private String ds;
}

