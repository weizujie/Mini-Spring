package com.spring.service;

import com.spring.Autowired;
import com.spring.Component;
import com.spring.InitializingBean;

/**
 * @author weizujie
 * @date 2021/11/17
 */
@Component("userService")
public class UserService implements InitializingBean {

    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化");
    }
}
