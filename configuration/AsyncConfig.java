package com.example.flim.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("VideoStreaming-");
        executor.initialize();
        return executor;
    }
//    CorePoolSize là số lượng luồng tối thiểu sẽ được duy trì.
//    MaxPoolSize là số lượng luồng tối đa mà Executor có thể tạo ra.
//    QueueCapacity là số lượng yêu cầu có thể xếp hàng chờ trước khi tạo thêm luồng mới.
}
