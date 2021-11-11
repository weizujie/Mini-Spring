# mini-spring

spring 框架学习

## spring-ioc

### 说明

根据 B 站 up 主：【楠哥教你学Java】的教学视频一步一步实现的基于注解的 Spring IoC 容器。

### 参考资料

- [bilibili-楠哥教你学Java-手写框架！3小时搞懂Spring IoC核心源码](https://www.bilibili.com/video/BV1AV411i7VH)


### 实现思路

1. 自定义一个 AnnotationConfigApplicationContext，构造器传入要扫描的包的全路径（如com.example.service）

2. 获取到这个包下的所有类

3. 遍历这些类，获取到有 @Component 注解的类，获取它的 Class 和对应的 beanName，封装成一个 BeanDefinition，存入 Set 集合。

4. 遍历集合，通过反射机制创建对象，同时检测对象的属性有没有添加 @Value 注解，如果有就给这些属性赋值，再将这些动态创建的对象以 key-value 的形式存入缓存区（这里用 HashMap)

5. 提供 getBean() 等方法，通过 beanName 取出对应的 bean
