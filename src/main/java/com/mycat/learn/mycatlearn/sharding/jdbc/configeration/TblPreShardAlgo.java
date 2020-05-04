package com.mycat.learn.mycatlearn.sharding.jdbc.configeration;


import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * 精准查询使用得分片算法
 *
 */
public class TblPreShardAlgo implements PreciseShardingAlgorithm<Long> {
    /**
     * 就是根据 preciseShardingValue 分片键 计算 数据路由到那个表中
     * @param collection
     * @param preciseShardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        //根据 分片键 计算路由到那个表
        if(preciseShardingValue.getValue()%2==0){
            //返回偶数名称得表
        }else {
            //返回奇数名称的表
        }
        return null;
    }
}
