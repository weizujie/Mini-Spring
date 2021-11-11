package com.ioc.annotation;

/**
 * 作用与属性，在一个 bean 中注入另一个 bean
 * @author weizujie
 * @date 2021/11/11
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Autowired {

}
