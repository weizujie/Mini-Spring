package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weizujie
 * @date 2021/11/17
 */
public class AnnotationConfigApplicationContext {

    private Class<?> configClass;

    private ConcurrentHashMap<String, Object> singletonObjectMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    private final String SINGLETON = "singleton";

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        this.configClass = configClass;

        scan(configClass);

        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (SINGLETON.equals(beanDefinition.getScope())) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjectMap.put(beanName, bean);
            }
        }
    }

    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (SINGLETON.equals(beanDefinition.getScope())) {
                return singletonObjectMap.get(beanName);
            } else {
                return createBean(beanName, beanDefinition);
            }
        } else {
            throw new NullPointerException();
        }

    }

    private void scan(Class<?> configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = configClass.getDeclaredAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();

            ClassLoader classLoader = AnnotationConfigApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path.replace(".", "/"));
            assert resource != null;
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                assert files != null;
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    if (fileName.endsWith(".class")) {
                        String className = path + "." + f.getName().substring(0, f.getName().indexOf(".class"));
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            if (clazz.isAnnotationPresent(Component.class)) {

                                // 是否实现了 BeanPostProcessor 接口
                                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                    BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                    beanPostProcessorList.add(beanPostProcessor);
                                }

                                Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                String beanName = componentAnnotation.value();

                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setClazz(clazz);

                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                } else {
                                    beanDefinition.setScope(SINGLETON);
                                }

                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class<?> clazz = beanDefinition.getClazz();

        try {
            Object obj = clazz.getDeclaredConstructor().newInstance();

            // 依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(field.getName());
                    if (bean == null) {
                        throw new NullPointerException();
                    }

                    field.setAccessible(true);
                    field.set(obj, bean);
                }
            }

            // Aware 回调
            if (obj instanceof BeanNameAware) {
                ((BeanNameAware) obj).setBeanName(beanName);
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(obj, beanName);
            }

            // 初始化
            if (obj instanceof InitializingBean) {
                try {
                    ((InitializingBean) obj).afterPropertiesSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessAfterInitialization(obj, beanName);
            }

            return obj;
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

}
