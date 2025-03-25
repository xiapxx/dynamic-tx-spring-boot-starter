package io.github.xiapxx.starter.dynamictx.transaction;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态事务
 *
 * @see SpringManagedTransaction
 * @Author xiapeng
 * @Date 2025-03-24 15:24
 */
public class DynamicManagedTransaction implements Transaction {

    private DynamicRoutingDataSource dynamicRoutingDataSource;

    private String defaultDataSourceKey;

    private Map<String, SpringManagedTransaction> dataSourceKey2TransactionMap;

    public DynamicManagedTransaction(DynamicRoutingDataSource dynamicRoutingDataSource, String defaultDataSourceKey) {
        Assert.notNull(dynamicRoutingDataSource, "No Dynamic Routing DataSource specified");
        Assert.hasLength(defaultDataSourceKey, "No Default DataSource Key specified");
        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
        this.defaultDataSourceKey = defaultDataSourceKey;
        this.dataSourceKey2TransactionMap = new HashMap<>();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getSpringManagedTransaction().getConnection();
    }

    /**
     * 获取当前数据源key对应的SpringManagedTransaction
     *
     * @return SpringManagedTransaction对象
     */
    private SpringManagedTransaction getSpringManagedTransaction() {
        String currentDataSourceKey = getCurrentDataSourceKey();
        SpringManagedTransaction springManagedTransaction = dataSourceKey2TransactionMap.get(currentDataSourceKey);
        if(springManagedTransaction == null){
            springManagedTransaction = new SpringManagedTransaction(dynamicRoutingDataSource.getDataSource(currentDataSourceKey));
            dataSourceKey2TransactionMap.put(currentDataSourceKey, springManagedTransaction);
        }
        return springManagedTransaction;
    }

    /**
     * 获取当前数据源key
     *
     * @return 当前数据源key
     */
    private String getCurrentDataSourceKey() {
        String currentDataSourceKey = DynamicDataSourceContextHolder.peek();
        return StringUtils.hasLength(currentDataSourceKey) ? currentDataSourceKey : defaultDataSourceKey;
    }

    @Override
    public void commit() throws SQLException {
        batchAction(springManagedTransaction -> springManagedTransaction.commit());
    }

    @Override
    public void rollback() throws SQLException {
        batchAction(springManagedTransaction -> springManagedTransaction.rollback());
    }

    @Override
    public void close() throws SQLException {
        batchAction(springManagedTransaction -> springManagedTransaction.close());
    }

    private void batchAction(TransactionAction transactionAction) throws SQLException {
        SQLException sqlException = null;
        for (SpringManagedTransaction springManagedTransaction : dataSourceKey2TransactionMap.values()) {
            try {
                transactionAction.execute(springManagedTransaction);
            } catch (SQLException e) {
                sqlException = e;
            }
        }
        if(sqlException != null){
            throw sqlException;
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return getSpringManagedTransaction().getTimeout();
    }

    @FunctionalInterface
    private interface TransactionAction {

        void execute(SpringManagedTransaction springManagedTransaction) throws SQLException;

    }
}
