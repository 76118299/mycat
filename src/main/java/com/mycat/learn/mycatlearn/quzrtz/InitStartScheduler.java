package com.mycat.learn.mycatlearn.quzrtz;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * springboot启动的时候启动任务
 */
@Component
public class InitStartScheduler implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        //1.调用server查询数据库
        //2.创建任务并启动


    }
}
