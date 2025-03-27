# dynamic-tx-spring-boot-starter

## 简介
    com.baomidou/dynamic-datasource-spring-boot-starter实现了数据源的动态切换, 但并没有实现事务的动态切换。
    该组件允许事务动态切换。

## 支持哪些场景？
    假设你的项目里有如下配置:
    spring.datasource.dynamic.primary=mysql1
    spring.datasource.dynamic.datasource.mysql1.*,
    spring.datasource.dynamic.datasource.mysql2.*

    场景1: 支持在数据源1的事务里, 调用数据源2的方法
    
    @Transactional("mysql1") 或 @Transactional   // 因为spring.datasource.dynamic.primary=mysql1, 所以不填的话, 默认就是mysql1
    public void test(){
        dataSource1Mapper.test();  // 该Mapper标注了@DS("mysql1")
        dataSource2Mapper.test(); // 该Mapper标注了@DS("mysql2")
    }

    场景2: 支持在数据源1的事务里, 调用数据源2的事务方法

    @Transactional("mysql1") 或 @Transactional   // 因为spring.datasource.dynamic.primary=mysql1, 所以不填的话, 默认就是mysql1
    public void test(){
        dataSource1Mapper.test();  // 该Mapper标注了@DS("mysql1")
        dataSource2Service.test(); // 该Service标注了@Transactional("mysql2")
    }

## 如何使用?
    使用该组件的前提条件是你的项目里已经在使用com.baomidou/dynamic-datasource-spring-boot-starter

### 引入依赖
~~~~xml
<dependency>
    <groupId>io.github.xiapxx</groupId>
    <artifactId>dynamic-tx-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
~~~~

### 开启动态事务

    // 该注解会自动生成mysql1和mysql2的事务管理器, 并且beanName等于mysql1或mysql2
    // 因为spring.datasource.dynamic.primary=mysql1, 所以mysql1的事务管理器的primary=true
    @EnableDynamicTx({"mysql1", "mysql2"})
    @Configuration
    public class XXXConfiguration {
        ...
    }
