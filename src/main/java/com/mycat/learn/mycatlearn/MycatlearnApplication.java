package com.mycat.learn.mycatlearn;

import com.mycat.learn.mycatlearn.datasource.DynamicDatasourceConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Import(DynamicDatasourceConfig.class)
@MapperScan( basePackages = "com.mycat.learn.mycatlearn.sharding.jdbc.mapper")
public class MycatlearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(MycatlearnApplication.class, args);
    }

}
