package com.mycat.learn.mycatlearn.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.zaxxer.hikari.util.DriverDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DynamicDatasourceConfig {
    /**
     * 数据源1
     * @return
     */
    @Bean
    @ConfigurationProperties("springdatasource.druid.first")
    public DataSource firstDatasource(){
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 数据源2
     * @return
     */
    @Bean
    @ConfigurationProperties("springdatasource.druid.send")
    public DataSource sendDatasource(){
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 创建动态数据源
     * @return
     */
    @Bean
    @Primary
    public DynamicDatasource dynamicDataSource(DataSource firstDataSource,DataSource sendDataSource){
        Map<Object,Object> objectObjectMap = new HashMap<>();
        objectObjectMap.put("fiest",firstDataSource);
        objectObjectMap.put("send",sendDataSource);

        return  new DynamicDatasource(firstDataSource,objectObjectMap);
    }
//mybatis  数据源配置
//    @Bean
//    @ConfigurationProperties(prefix = "mybatis")
//    public SqlSessionFactoryBean sqlSessionFactoryBean(){
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//        configuration.setMapUnderscoreToCamelCase(true); //下划线转骆驼
//
//        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
//        sqlSessionFactoryBean.setConfiguration(configuration);
//
//        return sqlSessionFactoryBean;
//    }

    /**
     * 事务
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dynamicDataSource(firstDatasource(),sendDatasource()));
    }


}
