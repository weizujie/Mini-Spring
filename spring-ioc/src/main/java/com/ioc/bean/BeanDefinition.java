package com.ioc.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author weizujie
 * @date 2021/11/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanDefinition {

    private String beanName;

    private Class beanClass;

}
