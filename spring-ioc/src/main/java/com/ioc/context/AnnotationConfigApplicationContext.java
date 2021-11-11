package com.ioc.context;

import com.ioc.annotation.Autowired;
import com.ioc.annotation.Component;
import com.ioc.annotation.Qualifier;
import com.ioc.annotation.Value;
import com.ioc.bean.BeanDefinition;
import com.ioc.tools.ClassScanner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author weizujie
 * @date 2021/11/10
 */
public class AnnotationConfigApplicationContext {

    /**
     * IoC 容器，用于存放 bean
     * key: beanName
     * value: bean
     */
    private Map<String, Object> cache = new LinkedHashMap<>();

    /**
     * 有参构造，传入包的全路径，如：com.example.controller
     * @param pack 包的全路径
     */
    public AnnotationConfigApplicationContext(String pack) {
        // 遍历包，找到目标类
        Set<BeanDefinition> beanDefinitions = findBeanDefinitions(pack);

        // 遍历集合，动态创建 bean
        createObject(beanDefinitions);

        // 自动装载 bean
        autowiredObject(beanDefinitions);
    }

    /**
     * 从容器中获取 bean
     * @param beanName beanName
     * @return bean
     */
    public Object getBean(String beanName) {
        return cache.get(beanName);
    }

    /**
     * 包扫描实现
     * @param pack 包的全路径
     * @return 存放 bean 的集合
     */
    private Set<BeanDefinition> findBeanDefinitions(String pack) {
        // 创建用于存放 BeanDefinition 对象的 Set 集合
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();

        // 获取指定包下的所有类
        Set<Class<?>> classes = ClassScanner.getClasses(pack);

        // 遍历这些类
        for (Class<?> clz : classes) {
            // 获取到有 @Component 注解的类
            Component componentAnnotation = clz.getAnnotation(Component.class);
            if (componentAnnotation != null) {
                // 获取 @Component 注解的值
                String beanName = componentAnnotation.value();

                // 如果没有设置值，以该类的首字母小写类名为 beanName
                if ("".equals(beanName)) {
                    // 获取类名首字母小写
                    String clzName = clz.getSimpleName();
                    beanName = clzName.substring(0, 1).toLowerCase() + clzName.substring(1);
                }

                // 封装成 BeanDefinition，并存入 Set 集合
                beanDefinitions.add(new BeanDefinition(beanName, clz));
            }
        }

        return beanDefinitions;
    }

    /**
     * 创建 bean
     * @param beanDefinitions 存放 bean 的集合
     */
    private void createObject(Set<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class clz = beanDefinition.getBeanClass();
            String beanName = beanDefinition.getBeanName();
            try {
                Object obj = clz.getConstructor().newInstance();
                cache.put(beanName, obj);

                Field[] fields = clz.getDeclaredFields();
                for (Field field : fields) {
                    Value valueAnnotation = field.getAnnotation(Value.class);
                    if (valueAnnotation != null) {
                        // 获取属性名
                        String fieldName = field.getName();

                        // 获取属性的 set 方法
                        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        Method setMethod = clz.getMethod(methodName, field.getType());

                        // 获取注解上的值
                        String value = valueAnnotation.value();

                        // 类型转换
                        Object val;
                        switch (field.getType().getSimpleName()) {
                            case "Integer":
                                val = Integer.parseInt(value);
                                break;
                            case "Long":
                                val = Long.parseLong(value);
                                break;
                            case "Double":
                                val = Double.parseDouble(value);
                                break;
                            case "Short":
                                val = Short.parseShort(value);
                                break;
                            case "Float":
                                val = Float.parseFloat(value);
                                break;
                            case "Boolean":
                                val = Boolean.parseBoolean(value);
                                break;
                            case "String":
                                val = value;
                                break;
                            default:
                                throw new RuntimeException("数据类型转换异常");
                        }

                        // 调用 set 方法给属性赋值
                        setMethod.invoke(obj, val);
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 自动装配实现
     * @param beanDefinitions 存放 bean 的集合
     */
    private void autowiredObject(Set<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class clz = beanDefinition.getBeanClass();

            // 找到添加了 @Autowired 注解的属性
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
                if (autowiredAnnotation != null) {
                    // 找到添加了 @Qualifier 注解的属性
                    Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                    if (qualifierAnnotation != null) {
                        // 获取 @Qualifier 注解的值，这个值就是 beanName
                        String beanName = qualifierAnnotation.value();

                        // 从容器中找到这个 bean
                        Object bean = getBean(beanName);

                        // 获取 set 方法
                        String fieldName = field.getName();
                        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                        try {
                            Method method = clz.getMethod(methodName, field.getType());

                            // 给 bean 赋值（这个 obj 是对象，bean 是属性）
                            // 在 obj 里装载 bean
                            Object obj = getBean(beanDefinition.getBeanName());
                            method.invoke(obj, bean);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
