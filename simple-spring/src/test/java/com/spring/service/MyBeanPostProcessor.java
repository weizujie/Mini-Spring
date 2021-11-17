package com.spring.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

/**
 * @author weizujie
 * @date 2021/11/17
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        if ("userService".equals(beanName)) {
            System.out.println("初始化前，我直接打印帅比");
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ("userService".equals(beanName)) {
            System.out.println("初始化后，芜湖起飞了兄弟们");
        }

        return bean;
    }
}
