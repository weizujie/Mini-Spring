package com.spring;

/**
 * @author weizujie
 * @date 2021/11/17
 */
public interface InitializingBean {

    void afterPropertiesSet() throws Exception;

}
