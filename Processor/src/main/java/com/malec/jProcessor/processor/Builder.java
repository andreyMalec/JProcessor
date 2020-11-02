package com.malec.jProcessor.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR})
public @interface Builder {
    String var1() default "";

    String var2() default "";

    String var3() default "";

    String var4() default "";

    String var5() default "";

    String var6() default "";

    String var7() default "";
}