package com.example.threadvisualizer.model;

public class PoolStats {
    private int activeThreads;
    private int corePoolSize;
    private int maxPoolSize;
    private int poolSize;
    private int queueCapacity;
    private int queueRemainingCapacity;
    private int currentQueueSize;
    private long completedTasks;
    private long rejectedTasks;
    private long totalSubmittedTasks;

    public PoolStats() {}

    public int getActiveThreads() {
        return activeThreads;
    }

    public void setActiveThreads(int activeThreads) {
        this.activeThreads = activeThreads;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getQueueRemainingCapacity() {
        return queueRemainingCapacity;
    }

    public void setQueueRemainingCapacity(int queueRemainingCapacity) {
        this.queueRemainingCapacity = queueRemainingCapacity;
    }

    public int getCurrentQueueSize() {
        return currentQueueSize;
    }

    public void setCurrentQueueSize(int currentQueueSize) {
        this.currentQueueSize = currentQueueSize;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getRejectedTasks() {
        return rejectedTasks;
    }

    public void setRejectedTasks(long rejectedTasks) {
        this.rejectedTasks = rejectedTasks;
    }

    public long getTotalSubmittedTasks() {
        return totalSubmittedTasks;
    }

    public void setTotalSubmittedTasks(long totalSubmittedTasks) {
        this.totalSubmittedTasks = totalSubmittedTasks;
    }
}

