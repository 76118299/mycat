package com.mycat.learn.mycatlearn.quzrtz;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Configuration
public class QuartzConfig {
    @Autowired
    private MyJobFactory1 jobFactory1;


    @Bean(name="schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory1);
        return schedulerFactoryBean;
    }
    @Bean(name="scheduler")
    public Scheduler scheduler(){
        return  schedulerFactoryBean().getScheduler();
    }

}
