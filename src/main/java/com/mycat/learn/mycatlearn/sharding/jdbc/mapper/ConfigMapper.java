package com.mycat.learn.mycatlearn.sharding.jdbc.mapper;


import com.mycat.learn.mycatlearn.sharding.jdbc.entity.Config;

public interface ConfigMapper {
    int deleteByPrimaryKey(Integer configId);

    int insert(Config record);

    int insertSelective(Config record);

    Config selectByPrimaryKey(Integer configId);

    int updateByPrimaryKeySelective(Config record);

    int updateByPrimaryKey(Config record);
}