package com.spring;

/**
 * @author weizujie
 * @date 2021/11/11
 */
public class ApiTest {

    public static void main(String[] args) {
        // 1.创建 bean 工厂
        BeanFactory beanFactory = new BeanFactory();

        // 2.注册 bean
        BeanDefinition beanDefinition = new BeanDefinition(new UserService());
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 3.获取 bean
        UserService userService = (UserService)beanFactory.getBean("userService");
        userService.getUserInfo();
    }
}
