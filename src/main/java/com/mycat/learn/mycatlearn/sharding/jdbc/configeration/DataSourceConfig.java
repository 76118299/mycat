package com.mycat.learn.mycatlearn.sharding.jdbc.configeration;



import com.google.common.collect.Lists;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Configuration
public class DataSourceConfig {

    private DataSource getShardingDataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        //创建绑定表
        shardingRuleConfiguration.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        shardingRuleConfiguration.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());
        shardingRuleConfiguration.getBindingTableGroups().add("t_order,t_order_item");
        // 创建广播表
        shardingRuleConfiguration.getBroadcastTables().add("t_config");
        // 默认的分库策略
        shardingRuleConfiguration.setDefaultDatabaseShardingStrategyConfig(
                new InlineShardingStrategyConfiguration("user_id","ds${user_id$2}"));
        //默认的分表策略
        shardingRuleConfiguration.setDefaultTableShardingStrategyConfig(
                new StandardShardingStrategyConfiguration("order_id",new TblPreShardAlgo()));

        //配置读写分离
        shardingRuleConfiguration.setMasterSlaveRuleConfigs(getMasterSlaveRuleConfigurations());
        //全局id生成策略
        shardingRuleConfiguration.setDefaultKeyGeneratorConfig(getKeyGeneratorConfiguration());

        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(),shardingRuleConfiguration,new Properties());

    }

    /**
     * 创建分片key
     * @return
     */
    private static KeyGeneratorConfiguration getKeyGeneratorConfiguration(){
        KeyGeneratorConfiguration result = new KeyGeneratorConfiguration("SNOWFLAKE","order_id");
        return  result;
    }
    private TableRuleConfiguration getOrderTableRuleConfiguration(){
        TableRuleConfiguration result = new TableRuleConfiguration("t_order","ds${0..1}.t_order${0..1}");
        //配置分片键
        result.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("t_order",new TblPreShardAlgo()));
        return  result;
    }
    /**
     * 创建逻辑表
     * @return
     */
    private TableRuleConfiguration getOrderItemTableRuleConfiguration(){
        TableRuleConfiguration result = new TableRuleConfiguration("t_order_item","ds${0..1}.t_order_item${0..1}");

        return  result;
    }


    private Map<String ,DataSource> createDataSourceMap(){
        Map<String,DataSource> result = new HashMap<>();
        result.put("db1",createDataSource("jdbc:mysql//localhost:3306/db1"));
        result.put("db2",createDataSource("jdbc:mysql//localhost:3306/db2"));
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

    List<MasterSlaveRuleConfiguration> getMasterSlaveRuleConfigurations() {
        MasterSlaveRuleConfiguration masterSlaveRuleConfig1 = new MasterSlaveRuleConfiguration("ds_0", "demo_ds_master_0", Arrays.asList("demo_ds_master_0_slave_0", "demo_ds_master_0_slave_1"));
        MasterSlaveRuleConfiguration masterSlaveRuleConfig2 = new MasterSlaveRuleConfiguration("ds_1", "demo_ds_master_1", Arrays.asList("demo_ds_master_1_slave_0", "demo_ds_master_1_slave_1"));
        return Lists.newArrayList(masterSlaveRuleConfig1, masterSlaveRuleConfig2);
    }



}
