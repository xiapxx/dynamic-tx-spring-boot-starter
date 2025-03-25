package io.github.xiapxx.starter.dynamictx.transaction;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;
import javax.sql.DataSource;

/**
 * @Author xiapeng
 * @Date 2025-03-24 17:03
 */
public class DynamicManagedTransactionFactoryRegister implements BeanPostProcessor {

    private String defaultDataSourceKey;

    public DynamicManagedTransactionFactoryRegister(String defaultDataSourceKey) {
        this.defaultDataSourceKey = defaultDataSourceKey;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof SqlSessionFactory){
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) bean;
            Configuration configuration = sqlSessionFactory.getConfiguration();
            DataSource dataSource = configuration.getEnvironment().getDataSource();
            Assert.isTrue(dataSource instanceof DynamicRoutingDataSource, "未指定动态数据源: DynamicRoutingDataSource");

            configuration.setEnvironment(new Environment(MybatisSqlSessionFactoryBean.class.getSimpleName(),
                            new DynamicManagedTransactionFactory(defaultDataSourceKey), dataSource)
                    );

        }
        return bean;
    }
}
