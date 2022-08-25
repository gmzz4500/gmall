package com.yyds.gmall.item.config;

/**
 * @ClassName: GmallDefaultThreadPool
 * @Author: yyd
 * @Date: 2022/8/3/003
 * @Description:
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/***
 * 自定义线程池
 */
@Configuration
public class GmallDefaultThreadPool {
    /**
     * 自定义的线程池
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(
                16,
                32,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000)
        );
    }
}
