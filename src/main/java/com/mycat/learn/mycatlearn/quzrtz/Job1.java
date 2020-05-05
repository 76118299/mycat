package com.mycat.learn.mycatlearn.quzrtz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 任务
 */
public class Job1 implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.printf("aaaaaaaaa");
    }
}
