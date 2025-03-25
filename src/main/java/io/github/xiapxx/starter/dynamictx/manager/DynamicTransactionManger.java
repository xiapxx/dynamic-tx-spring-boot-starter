package io.github.xiapxx.starter.dynamictx.manager;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import javax.sql.DataSource;

/**
 * @Author xiapeng
 * @Date 2025-03-24 14:32
 */
public class DynamicTransactionManger extends DataSourceTransactionManager {

    private String dataSourceKey;

    private DataSource actualDataSource;

    public DynamicTransactionManger(String dataSourceKey){
        this.dataSourceKey = dataSourceKey;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        loadActualDataSource();
    }

    /**
     * 提取数据源
     * 事务层面, 不使用DynamicRoutingDataSource, 改为使用实际的数据源
     *
     * @return 实际的数据源
     */
    @Override
    protected DataSource obtainDataSource() {
        return actualDataSource;
    }

    private void loadActualDataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) getDataSource();
        this.actualDataSource = dynamicRoutingDataSource.getDataSource(dataSourceKey);
        Assert.notNull(actualDataSource, "无效的dataSourceKey: " + dataSourceKey);
    }
}
