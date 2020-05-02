package com.mycat.learn.mycatlearn.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class DataSourceAspect {

    @Pointcut("@annotation(com.mycat.learn.mycatlearn.datasource.TargetDatasource)")
    public void dataSourcePointCut(){}


    @Around("dataSourcePointCut()")
    public Object aroud(ProceedingJoinPoint proceedingJoinPoint){
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        TargetDatasource annotation = method.getAnnotation(TargetDatasource.class);
        if(annotation == null){
            DynamicDatasource.setDatasource("first");
        }else {
            DynamicDatasource.setDatasource(annotation.name());
        }
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }finally {
            DynamicDatasource.clearDatasource();
        }
        return  null;
    }
}
