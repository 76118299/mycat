package com.mycat.learn.mycatlearn.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源路由
 */
public class DynamicDatasource extends AbstractRoutingDataSource {
    private static  final  ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public DynamicDatasource(DataSource defautlTargetDataSource, Map<Object,Object> targetDataSource) {
        super.setDefaultTargetDataSource(defautlTargetDataSource);
        super.setTargetDataSources(targetDataSource);
        super.afterPropertiesSet();
    }

    public static void setDatasource(String datasource){
        CONTEXT_HOLDER.set(datasource);
    }
    public static String getDatasource(){
        return  CONTEXT_HOLDER.get();
    }
    public static void clearDatasource(){
        CONTEXT_HOLDER.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDatasource();
    }
}
