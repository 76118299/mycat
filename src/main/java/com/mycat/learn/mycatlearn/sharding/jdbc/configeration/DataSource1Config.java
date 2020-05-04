package com.mycat.learn.mycatlearn.sharding.jdbc.configeration;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * sharding 读写分离
 */
@Configuration
public class DataSource1Config {

    public DataSource getMasterSlaveConfig() throws SQLException {
        MasterSlaveRuleConfiguration masterSlaveRuleConfiguration = new
                MasterSlaveRuleConfiguration("ds_master_slave","ds_master", Arrays.asList("ds_slave0","ds_slave1"));
        return MasterSlaveDataSourceFactory.createDataSource(createDataSourceMap(),masterSlaveRuleConfiguration,new Properties());
    }

    private Map<String, DataSource> createDataSourceMap(){
        Map<String , DataSource > result = new HashMap<>();
        result.put("ds_master",createDataSource("jdbc:mysql//localhost:3306/db1") );
        result.put("ds_slave0",createDataSource("jdbc:mysql//localhost:3306/db2") );
        result.put("ds_slave1",createDataSource("jdbc:mysql//localhost:3306/db3") );
        return  result;
    }


    /**
     * 创建数据源
     * @param dataSourceName
     * @return
     */
    private DataSource createDataSource(final  String dataSourceName){
        BasicDataSource result = new BasicDataSource();
        result.setDriverClassName("com.mysql.jdbc.Driver");
        result.setUrl(dataSourceName);
        result.setUsername("root");
        result.setPassword("123456");
        return  result;
    }
}
