package com.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weizujie
 * @date 2021/11/11
 */
public class BeanFactory {

    /**
     * 存放 bean 的集合
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 获取 bean 实例
     * @param name bean 名称
     * @return bean 实例
     */
    public Object getBean(String name) {
        return beanDefinitionMap.get(name).getBean();
    }

    /**
     * 注册 bean
     * @param name bean 名称
     * @param beanDefinition 存放 bean 信息的对象
     */
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }

}
