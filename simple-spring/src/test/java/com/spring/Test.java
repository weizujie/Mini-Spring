package com.spring;

import com.spring.service.UserService;

/**
 * @author weizujie
 * @date 2021/11/17
 */
public class Test {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = (UserService) context.getBean("userService");
        userService.test();
    }

}
