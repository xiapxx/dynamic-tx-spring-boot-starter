package io.github.xiapxx.starter.dynamictx.transaction;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import javax.sql.DataSource;

/**
 * @Author xiapeng
 * @Date 2025-03-24 15:21
 */
public class DynamicManagedTransactionFactory extends SpringManagedTransactionFactory {

    private String defaultDataSourceKey;

    public DynamicManagedTransactionFactory(String defaultDataSourceKey) {
        this.defaultDataSourceKey = defaultDataSourceKey;
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        DynamicRoutingDataSource routingDataSource = (DynamicRoutingDataSource) dataSource;
        return new DynamicManagedTransaction(routingDataSource, defaultDataSourceKey);
    }
}
