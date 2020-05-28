package com.yx.dynamic.redis.matcher;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Auther: shiyongxi
 * @Date: 2020-05-28 15:05
 * @Description: ExpressionMatcher
 */
@AllArgsConstructor
@Data
public class ExpressionMatcher implements Matcher {
    private String expression;

    private String ds;
}