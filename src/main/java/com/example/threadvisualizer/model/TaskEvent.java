package com.example.threadvisualizer.model;

public class TaskEvent {
    private String taskId;
    private String threadName;
    private String status; // QUEUED, RUNNING, COMPLETED, REJECTED
    private int progress;  // 0 - 100
    private String message;
    private long timestamp;

    public TaskEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public TaskEvent(String taskId, String threadName, String status, int progress, String message) {
        this.taskId = taskId;
        this.threadName = threadName;
        this.status = status;
        this.progress = progress;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

