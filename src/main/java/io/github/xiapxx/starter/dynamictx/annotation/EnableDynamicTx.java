package io.github.xiapxx.starter.dynamictx.annotation;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import io.github.xiapxx.starter.dynamictx.DynamicTransactionMangerRegister;
import org.springframework.context.annotation.Import;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启动态事务管理器
 *
 * @Author xiapeng
 * @Date 2025-03-24 14:16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DynamicTransactionMangerRegister.class)
public @interface EnableDynamicTx {

    /**
     * 假设: 配置文件中有  spring.datasource.dynamic.datasource.mysqlAbc.*,
     *                  spring.datasource.dynamic.datasource.mysqlCde.* 的相关配置,
     * 那么: 此处配置可配置成mysqlAbc, mysqlCde
     * @return
     */
    String[] value();

    /**
     * 动态数据源的名称
     *
     * @see DynamicRoutingDataSource
     * @return 动态数据源名称
     */
    String dynamicDataSourceName() default "dataSource";

}
