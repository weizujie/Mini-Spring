package com.spring;

/**
 * @author weizujie
 * @date 2021/11/17
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName);

    Object postProcessAfterInitialization(Object bean, String beanName);

}
