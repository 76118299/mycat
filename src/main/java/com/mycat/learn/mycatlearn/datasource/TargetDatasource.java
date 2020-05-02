package com.mycat.learn.mycatlearn.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDatasource {
    String name () default "";
}
