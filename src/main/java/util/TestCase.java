/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-19
 *  * Update Date : 2019-1-19
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 轻量化自动测试的标记注解...
 */

package util;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestCase { }