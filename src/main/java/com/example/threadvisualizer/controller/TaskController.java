package com.example.threadvisualizer.controller;

import com.example.threadvisualizer.config.ThreadPoolConfig;
import com.example.threadvisualizer.model.PoolStats;
import com.example.threadvisualizer.model.TaskEvent;
import com.example.threadvisualizer.service.AsyncTaskService;
import com.example.threadvisualizer.service.SseBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final AsyncTaskService taskService;
    private final ThreadPoolConfig threadPoolConfig;
    private final SseBroadcaster broadcaster;
    private final AtomicInteger taskCounter = new AtomicInteger(100);

    public TaskController(AsyncTaskService taskService,
                          ThreadPoolConfig threadPoolConfig,
                          SseBroadcaster broadcaster) {
        this.taskService = taskService;
        this.threadPoolConfig = threadPoolConfig;
        this.broadcaster = broadcaster;
    }

    @GetMapping("/stream")
    public SseEmitter streamEvents() {
        SseEmitter emitter = broadcaster.registerClient();
        // Immediately send initial stats to new subscriber
        broadcaster.broadcast("pool-stats", threadPoolConfig.getStats());
        return emitter;
    }

    @PostMapping("/tasks")
    public ResponseEntity<Map<String, Object>> submitTasks(
            @RequestParam(defaultValue = "1") int count,
            @RequestParam(defaultValue = "4") int duration) {

        int submitted = 0;
        int rejected = 0;

        for (int i = 0; i < count; i++) {
            String taskId = "TASK-" + taskCounter.incrementAndGet();
            threadPoolConfig.incrementSubmitted();

            // Notify UI that task was submitted and placed in queue / submitted to executor
            TaskEvent queuedEvent = new TaskEvent(
                    taskId,
                    "Pending Dispatch",
                    "QUEUED",
                    0,
                    "Submitted to thread pool (in queue or awaiting worker thread)"
            );
            broadcaster.broadcast("task-update", queuedEvent);
            broadcaster.broadcast("pool-stats", threadPoolConfig.getStats());

            try {
                taskService.runSimulatedTask(taskId, duration);
                submitted++;
            } catch (RejectedExecutionException e) {
                log.warn("Task {} rejected by executor", taskId);
                rejected++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("submitted", submitted);
        response.put("rejected", rejected);
        response.put("message", "Processed " + count + " task submission(s)");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<PoolStats> getStats() {
        return ResponseEntity.ok(threadPoolConfig.getStats());
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> reset() {
        threadPoolConfig.resetMetrics();
        broadcaster.broadcast("pool-stats", threadPoolConfig.getStats());
        Map<String, String> response = new HashMap<>();
        response.put("status", "Reset complete");
        return ResponseEntity.ok(response);
    }
}

