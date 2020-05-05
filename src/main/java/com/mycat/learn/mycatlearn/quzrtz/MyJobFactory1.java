package com.mycat.learn.mycatlearn.quzrtz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * 解决任务无法注入springBean问题
 */
@Component
public class MyJobFactory1 extends SpringBeanJobFactory implements ApplicationContextAware {
    @Autowired
    private AutowireCapableBeanFactory factory;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.factory = applicationContext.getAutowireCapableBeanFactory();

    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        factory.autowireBean(jobInstance);
        return jobInstance;
    }
}
