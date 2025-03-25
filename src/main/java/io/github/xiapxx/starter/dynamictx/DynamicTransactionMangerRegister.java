package io.github.xiapxx.starter.dynamictx;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import io.github.xiapxx.starter.dynamictx.annotation.EnableDynamicTx;
import io.github.xiapxx.starter.dynamictx.manager.DynamicTransactionManger;
import io.github.xiapxx.starter.dynamictx.transaction.DynamicManagedTransactionFactoryRegister;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author xiapeng
 * @Date 2025-03-24 14:20
 */
@ConditionalOnClass({DynamicRoutingDataSource.class, MybatisSqlSessionFactoryBean.class})
public class DynamicTransactionMangerRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static final String PRIMARY_KEY = DynamicDataSourceProperties.PREFIX + ".primary";
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableDynamicTx.class.getName()));
        String defaultDataSourceKey = environment.getProperty(PRIMARY_KEY);
        registerDynamicTransactionManger(registry,
                annoAttrs.getString("dynamicDataSourceName"),
                defaultDataSourceKey,
                annoAttrs.getStringArray("value"));

        registerDynamicManagedTransactionFactory(registry, defaultDataSourceKey);
    }

    /**
     * 注册事务工厂
     *
     * @param registry registry
     * @param defaultDataSourceKey defaultDataSourceKey
     */
    private void registerDynamicManagedTransactionFactory(BeanDefinitionRegistry registry, String defaultDataSourceKey) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DynamicManagedTransactionFactoryRegister.class);
        beanDefinitionBuilder.addConstructorArgValue(defaultDataSourceKey);
        registry.registerBeanDefinition(DynamicManagedTransactionFactoryRegister.class.getName(), beanDefinitionBuilder.getBeanDefinition());
    }

    /**
     * 注册动态事务管理器
     *
     * @param registry registry
     * @param dynamicDataSourceName dynamicDataSourceName
     * @param dataSourceKeys dataSourceKeys
     */
    private void registerDynamicTransactionManger(BeanDefinitionRegistry registry,
                                                  String dynamicDataSourceName,
                                                  String defaultDataSourceKey,
                                                  String[] dataSourceKeys) {
        for (String dataSourceKey : dataSourceKeys) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DynamicTransactionManger.class);
            beanDefinitionBuilder.addConstructorArgValue(dataSourceKey);
            beanDefinitionBuilder.addPropertyReference("dataSource", dynamicDataSourceName);

            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            if(defaultDataSourceKey != null && defaultDataSourceKey.equals(dataSourceKey)){
                beanDefinition.setPrimary(true);
            }
            registry.registerBeanDefinition(dataSourceKey, beanDefinition);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
