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

## mini-spring

### mini-spring-step-01

#### 说明

创建最简的 Bean 容器，用于定义、存放和获取 Bean 对象。

#### 参考资料

- [实现一个简单的Bean容器](https://bugstack.cn/md/spring/develop-spring/2021-05-20-%E7%AC%AC2%E7%AB%A0%EF%BC%9A%E5%B0%8F%E8%AF%95%E7%89%9B%E5%88%80%EF%BC%8C%E5%AE%9E%E7%8E%B0%E4%B8%80%E4%B8%AA%E7%AE%80%E5%8D%95%E7%9A%84Bean%E5%AE%B9%E5%99%A8.html)

#### 实现思路

凡是可以存放数据的具体数据结构实现，都可以成为容器。在 Spring Bean 容器下，需要一个可以用于存放和名称索引式的数据结构，这里选择 HashMap。

- 定义：BeanDefinition，目前只包含一个 Object 类型的属性 bean，用于存放对象。

- 注册：相当于把数据存到 HashMap 中，目前只存放定义了 Bean 的对象信息。

- 获取：Bean 的名字就是 key，Spring 容器初始化好 Bean 后，就可以直接获取。
