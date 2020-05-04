package com.mycat.learn.mycatlearn.sharding.jdbc.mapper;


import com.mycat.learn.mycatlearn.sharding.jdbc.entity.Order;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
}