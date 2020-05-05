package com.mycat.learn.mycatlearn.quzrtz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.AnnualCalendar;
import org.quartz.impl.matchers.EverythingMatcher;

import java.util.GregorianCalendar;


public class scheduler {
    public static void main(String[] args) throws SchedulerException {
        //创建任务调度器
        SchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(Job1.class)
                //分配组和任务名称
                .withIdentity("job1","group1")
                //携带其他数据
                .usingJobData("zgc","123456")
                .usingJobData("moon",5.21f)
                .build();
        //创建触发器
        /**
         * simpleSchedule 基于时刻的调度器（毫秒）
         * CalendarIntervalTrigger 基于日期 年月日
         * CronTrigger 基于表达式
         *  *（秒）*（分）*（时）*（日）*（月）*（星期） *（年）
         *  /2****？
         * DailyTimeIntervalTrigger 基于某天的时间段 （每天8点，每天5点）
         */

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1","group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(2)//2秒运行一次
                        //持续不断
                .repeatForever())
                .build();
        //绑定任务
        //一个任务可以绑定多个触发器
        scheduler.scheduleJob(jobDetail,trigger);
        //启动定时任务
        scheduler.start();

        /*
         * 排除规则的定义
         * AnnualCalendar 年排除（一年内的某个日期不执行）
         * CronCalendar
         * DailyCalendar 日
         * HolidayCalendar 节假日
         * MonthlyCalendar 每月
         * WeeklyCalendar  每周
         */

        AnnualCalendar annualCalendar = new AnnualCalendar();
        //排除10月1
        Calendar d = (Calendar) new GregorianCalendar(2020,10,1);
        annualCalendar.setDayExcluded((java.util.Calendar) d,true);
        //添加到调度器
        scheduler.addCalendar( "10.1",annualCalendar,false,false);

        //添加监听器
        //监听所有的任务
        scheduler.getListenerManager().addJobListener(new QuartzListener(), EverythingMatcher.allJobs());



    }

}

/**
 * 3类监听
 * JobListener
 * TriggerListener
 * SchedulerListener
 */
class QuartzListener implements JobListener{

    @Override
    public String getName() {
        return null;
    }

    /**
     * 任务即将执行
     * @param context
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        context.getJobDetail().getKey().getName();
    }

    /**
     * 任务被否决
     * @param context
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        context.getJobDetail().getKey().getName();
    }

    /**
     * 任务执行完成
     * @param context
     * @param jobException
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        context.getJobDetail().getKey().getName();
    }
}


















