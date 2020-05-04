package com.mycat.learn.mycatlearn.sharding.jdbc.configeration;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * 分库得策略 根据分片键 ,返回数据库名称
 */
public class DBShardAlgo  implements PreciseShardingAlgorithm<Long> {
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        String db_name = "ds";
     Long num=   preciseShardingValue.getValue()%2;
     db_name = db_name + num;
     for(String each : collection){
         if(each.equals(db_name)){
             return  each;
         }
     }
     throw new IllegalArgumentException();
    }
}
