package org.liubility.commons.zFeign;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Jdragon
 * @email: 1061917196@qq.com
 * @Date: 2020.11.22 23:20
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZFeign {

    String baseUrl();

    String basePath() default "";

    /**
     * @Description: 解构提取返回结果
     **/
    String[] depth() default {};

    String fallback() default "";
}
