package com.mycat.learn.mycatlearn.quzrtz;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.scheduling.quartz.DelegatingJob;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 *
 */
public class AdapTableJobFactory implements JobFactory {
    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {

        try {
            return newJob(bundle);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Job newJob(TriggerFiredBundle bundle) throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        Object jobObject = createJobInstance(bundle);

        return adaptJob(jobObject);
    }

    private Job adaptJob(Object jobObject) {

        if(jobObject instanceof Job){
            return (Job) jobObject;
        }else if(jobObject instanceof Runnable){
            return new DelegatingJob((Runnable) jobObject);
        }else {
            throw new IllegalArgumentException();
        }
    }

    protected Object createJobInstance(TriggerFiredBundle bundle) throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        Method getJobDetail = bundle.getClass().getMethod("getJobDetail");
        Object jboDetail = ReflectionUtils.invokeMethod(getJobDetail, bundle);
        Method getJobClass = jboDetail.getClass().getMethod("getJobClass");
        Class jobClass = (Class) ReflectionUtils.invokeMethod(getJobClass, jboDetail);

        return  jobClass.newInstance();
    }


}
