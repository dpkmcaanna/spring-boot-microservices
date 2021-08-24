package com.order.service.app.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Bean
    public ExecutorService traceableExecutorService(BeanFactory beanFactory) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        return new TraceableExecutorService(beanFactory, executorService);
    }
}
