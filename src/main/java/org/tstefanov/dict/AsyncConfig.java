package org.tstefanov.dict;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "fileUploadExecutor")
    public Executor fileUploadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Start with 5 threads
        executor.setMaxPoolSize(10); // Allow up to 10 threads
        executor.setQueueCapacity(25); // Queue up to 25 tasks before rejecting
        executor.setThreadNamePrefix("FileUpload-");
        executor.initialize();
        return executor;
    }
}
