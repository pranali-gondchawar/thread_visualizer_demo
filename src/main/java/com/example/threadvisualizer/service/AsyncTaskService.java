package com.example.threadvisualizer.service;

import com.example.threadvisualizer.config.ThreadPoolConfig;
import com.example.threadvisualizer.model.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncTaskService {

    private static final Logger log = LoggerFactory.getLogger(AsyncTaskService.class);

    private final SseBroadcaster broadcaster;
    private final ThreadPoolConfig threadPoolConfig;

    public AsyncTaskService(SseBroadcaster broadcaster, ThreadPoolConfig threadPoolConfig) {
        this.broadcaster = broadcaster;
        this.threadPoolConfig = threadPoolConfig;
    }

    @Async("customTaskExecutor")
    public CompletableFuture<String> runSimulatedTask(String taskId, int durationSeconds) {
        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();

        log.info("Task [{}] started on thread: [{}]", taskId, threadName);

        // 1. Announce task has begun execution
        TaskEvent startEvent = new TaskEvent(
                taskId,
                threadName,
                "RUNNING",
                0,
                "Picked up by " + threadName + " (Thread ID: " + threadId + ")"
        );
        broadcaster.broadcast("task-update", startEvent);
        broadcaster.broadcast("pool-stats", threadPoolConfig.getStats());

        // 2. Simulate step-by-step progress
        int steps = 10;
        int sleepPerStep = Math.max(100, (durationSeconds * 1000) / steps);

        try {
            for (int i = 1; i <= steps; i++) {
                Thread.sleep(sleepPerStep);
                int progress = i * 10;

                TaskEvent progressEvent = new TaskEvent(
                        taskId,
                        threadName,
                        i == steps ? "COMPLETED" : "RUNNING",
                        progress,
                        "Processing step " + i + "/" + steps + " on " + threadName
                );
                broadcaster.broadcast("task-update", progressEvent);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            TaskEvent errorEvent = new TaskEvent(
                    taskId,
                    threadName,
                    "ERROR",
                    0,
                    "Task interrupted on " + threadName
            );
            broadcaster.broadcast("task-update", errorEvent);
            return CompletableFuture.completedFuture("Interrupted");
        }

        log.info("Task [{}] finished on thread: [{}]", taskId, threadName);

        // 3. Announce completion & updated stats
        broadcaster.broadcast("pool-stats", threadPoolConfig.getStats());

        return CompletableFuture.completedFuture("Completed successfully on " + threadName);
    }
}

