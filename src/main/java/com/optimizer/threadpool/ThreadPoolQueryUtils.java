package com.optimizer.threadpool;

/***
 Created by nitish.goyal on 25/02/19
 ***/
@SuppressWarnings("WeakerAccess")
public class ThreadPoolQueryUtils {

    public static final String HYSTRIX_POOL_LIST_QUERY =
            "SHOW MEASUREMENTS with measurement = /phonepe.prod.%s.HystrixThreadPool.%s.propertyValue_corePoolSize/";

    public static final String HYSTRIX_POOL_NAME_PATTERN = "phonepe.prod.(.*).HystrixThreadPool.(.*).propertyValue_corePoolSize";

    public static final String CORE_POOL_QUERY =
            "SELECT max(\"value\") FROM \"phonepe.prod.%s.HystrixThreadPool.%s.propertyValue_corePoolSize\" WHERE time > now() - %sh " +
            "group by time(30s) fill(null)";

    public static final String POOL_USAGE_QUERY =
            "SELECT max(\"value\") FROM \"phonepe.prod.%s.HystrixThreadPool.%s" +
            ".rollingMaxActiveThreads\" WHERE time > now() - %sh group by time(30s) fill(null)";

}