package com.mycat.learn.mycatlearn.quzrtz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
@Component
public class SchedulerUtil {
    @Autowired
    private static MyJobFactory1 myJobFactory;
    @Autowired
    private Scheduler scheduler;//可以使用spring容器中的了

    public static void  addJob(String jobClassName,String jobName,String jobGroupName,String cronExpression,String jobDataMap) throws Exception {
        SchedulerFactory sf = new StdSchedulerFactory();
        //创建调度器
        Scheduler scheduler = sf.getScheduler();
      //  scheduler.setJobFactory(myJobFactory);
        scheduler.start();
        JobDetail jobDetail = JobBuilder.newJob(getClass(jobClassName).getClass())
                .withIdentity(jobName,jobGroupName).build();
        //创建cron表达式构造器 （任务的执行时间）
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        //创建触发器
      CronTrigger cronTrigger=  TriggerBuilder.newTrigger().withIdentity(jobName,jobGroupName)
                .withSchedule(scheduleBuilder).startNow().build();
      //绑定任务
        scheduler.scheduleJob(jobDetail,cronTrigger);



    }

    public static  void  stopJob(String jobName,String jobGroupName) throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        scheduler.pauseJob(JobKey.jobKey(jobName,jobGroupName));
    }
    public static void startJob(String jobName,String jobGroupName) throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        scheduler.resumeJob(JobKey.jobKey(jobName,jobGroupName));
    }

    public static void  deleteJob(String jobName,String jobGroupName) throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        //停止触发器
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobName,jobGroupName));
        //取消任务
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobName,jobGroupName));
        //删除任务
        scheduler.deleteJob(JobKey.jobKey(jobName,jobGroupName));
    }
    public static void  updateCronExpression(String jobName, String jobGroupName, String cronExpression) throws SchedulerException {
       SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        //创建表达式构造器
        CronScheduleBuilder cronScheduleBuilder =CronScheduleBuilder.cronSchedule(cronExpression);
        //获取cron触发器
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        //从新构建触发器
        trigger= trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).startNow().build();
        //重新设置job
        scheduler.rescheduleJob(triggerKey,trigger);


    }
    public static Boolean existsJob(String jobName, String jobGroupName) throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
      return   scheduler.checkExists(triggerKey);
    }
    public static void  stopAllJob() throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        scheduler.pauseAll();
    }

    public static void  startAllJob() throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        scheduler.resumeAll();
    }

public static BaseJob getClass(String clazz) throws Exception {
    try {
        Class<?> aClass = Class.forName(clazz);
        return (BaseJob) aClass.newInstance();
    } catch (Exception e) {
        throw new Exception("类["+clazz+"]不存在！");
    }


}

}
