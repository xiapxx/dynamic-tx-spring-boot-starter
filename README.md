# dynamic-tx-spring-boot-starter

## 简介
dynamic-datasource-spring-boot-starter实现了数据源的动态切换, 但并没有实现事务的动态切换。该组件允许事务动态切换

## 支持哪些场景？

    场景1: 支持在数据源1的事务里, 调用数据源2的方法
    
    @Transactional("数据源1")
    public void test(){
        dataSource1Mapper.test();  // 该Mapper标注了@DS("数据源1")
        dataSource2Mapper.test(); // 该Mapper标注了@DS("数据源2")
    }

    场景2: 支持在数据源1的事务里, 调用数据源2的事务方法

    @Transactional("数据源1")
    public void test(){
        dataSource1Mapper.test();  // 该Mapper标注了@DS("数据源1")
        dataSource2Service.test(); // 该Service标注了@Transactional("数据源2")
    }

## 如何使用?

### 引入依赖
~~~~xml
<dependency>
    <groupId>io.github.xiapxx</groupId>
    <artifactId>dynamic-tx-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
~~~~

### 开启动态事务
    假设有配置如下:
    spring.datasource.dynamic.datasource.mysql1.*,
    spring.datasource.dynamic.datasource.mysql2.* 

    开启:
    @EnableDynamicTx({"mysql1", "mysql2"})
    @Configuration
    public class XXXConfiguration {
        ...
    }
