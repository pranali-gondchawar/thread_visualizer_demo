package com.example.threadvisualizer.config;

import com.example.threadvisualizer.model.PoolStats;
import com.example.threadvisualizer.model.TaskEvent;
import com.example.threadvisualizer.service.SseBroadcaster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class ThreadPoolConfig {

    @Value("${threadpool.core-size:2}")
    private int corePoolSize;

    @Value("${threadpool.max-size:4}")
    private int maxPoolSize;

    @Value("${threadpool.queue-capacity:4}")
    private int queueCapacity;

    @Value("${threadpool.thread-name-prefix:worker-thread-}")
    private String threadNamePrefix;

    private final AtomicLong rejectedTaskCount = new AtomicLong(0);
    private final AtomicLong totalSubmittedCount = new AtomicLong(0);
    private ThreadPoolTaskExecutor executor;

    @Bean(name = "customTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor(SseBroadcaster broadcaster) {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // Custom RejectedExecutionHandler to demonstrate what happens when pool + queue are full
        executor.setRejectedExecutionHandler((runnable, exec) -> {
            long rejectedCount = rejectedTaskCount.incrementAndGet();
            TaskEvent rejectedEvent = new TaskEvent(
                    "rejected-" + rejectedCount,
                    "N/A",
                    "REJECTED",
                    0,
                    "Task rejected! ThreadPool is fully saturated (Queue full & Max Threads busy)."
            );
            broadcaster.broadcast("task-update", rejectedEvent);
            broadcaster.broadcast("pool-stats", getStats());
            throw new RejectedExecutionException("Task rejected: Thread pool capacity exceeded.");
        });

        executor.initialize();
        return executor;
    }

    public void incrementSubmitted() {
        totalSubmittedCount.incrementAndGet();
    }

    public void resetMetrics() {
        rejectedTaskCount.set(0);
        totalSubmittedCount.set(0);
    }

    public PoolStats getStats() {
        PoolStats stats = new PoolStats();
        if (executor != null && executor.getThreadPoolExecutor() != null) {
            ThreadPoolExecutor underlying = executor.getThreadPoolExecutor();
            stats.setActiveThreads(underlying.getActiveCount());
            stats.setPoolSize(underlying.getPoolSize());
            stats.setCorePoolSize(underlying.getCorePoolSize());
            stats.setMaxPoolSize(underlying.getMaximumPoolSize());
            stats.setQueueCapacity(queueCapacity);
            stats.setCurrentQueueSize(underlying.getQueue().size());
            stats.setQueueRemainingCapacity(underlying.getQueue().remainingCapacity());
            stats.setCompletedTasks(underlying.getCompletedTaskCount());
            stats.setRejectedTasks(rejectedTaskCount.get());
            stats.setTotalSubmittedTasks(totalSubmittedCount.get());
        }
        return stats;
    }
}

