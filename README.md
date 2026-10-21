# ⚡ ThreadPulse — Multithreading Visualizer

An interactive project to deeply understand **Java Multithreading, Thread Pools, and Asynchronous Processing** in **Spring Boot**, with a real-time reactive dashboard built in **Vue 3**.

---

## 🎯 What Core Multithreading Concepts Are Demonstrated?

### 1. `ThreadPoolTaskExecutor` Architecture
Spring Boot's `ThreadPoolTaskExecutor` wraps Java's `ThreadPoolExecutor`. The project configures:
- **Core Pool Size = 2**: Baseline dedicated worker threads (`worker-thread-1`, `worker-thread-2`).
- **Queue Capacity = 4**: When core threads are occupied, tasks wait in this queue.
- **Max Pool Size = 4**: When both core threads AND the queue are completely full, the pool spawns temporary extra worker threads (`worker-thread-3`, `worker-thread-4`).
- **Rejection Handler**: When Queue is full (4) + Max Pool is saturated (4 threads running) = 8 tasks active. Any further task triggers the `RejectedExecutionHandler`.

### 2. `@Async` and Non-blocking Execution
Tasks run asynchronously on worker threads without blocking the main HTTP web thread.

### 3. Server-Sent Events (`SseEmitter`)
Instead of polling the server, background threads broadcast their live progress (0% ➔ 100%), thread name, and thread pool metrics directly to the Vue 3 frontend in real time.

---

## 🚀 How to Run the Project

### Option A: Using Your Favorite IDE (IntelliJ IDEA / VS Code / Eclipse)
1. Open the folder `thread-visualizer` in your IDE.
2. Ensure you have Java 17+ selected.
3. Locate `src/main/java/com/example/threadvisualizer/ThreadVisualizerApplication.java`.
4. Right-click and choose **Run 'ThreadVisualizerApplication'**.
5. Open your browser at:
   ```
   http://localhost:8080
   ```

### Option B: Using Maven in Terminal
```bash
mvn spring-boot:run
```
Then navigate to `http://localhost:8080`.

---

## 🧪 Experiments You Can Run in the Dashboard

| Experiment | Action | What to Observe |
|---|---|---|
| **1. Baseline Thread Execution** | Click **"+ Submit 1 Task"** | Notice `worker-thread-1` changes to `RUNNING` with an animated progress bar. Thread 2 remains `IDLE`. |
| **2. Queue Saturation** | Click **"+ Submit 3 Tasks Burst"** | Both Core threads (`worker-thread-1` and `worker-thread-2`) will be busy, and the remaining tasks enter the `QUEUED` state. As soon as a worker finishes, it picks up the next task from the queue! |
| **3. Max Pool Spawning & Rejection** | Click **"⚡ Submit 10 Tasks Burst"** | 2 core threads run + 4 tasks fill the queue + 2 extra threads spawn (`worker-thread-3` & `4`) = 8 tasks total. The 9th and 10th tasks trigger **Task Rejection**! |

---

## 📂 Project Structure

```
thread-visualizer/
├── pom.xml
└── src/
    └── main/
        ├── java/com/example/threadvisualizer/
        │   ├── ThreadVisualizerApplication.java   # App entry point with @EnableAsync
        │   ├── config/
        │   │   └── ThreadPoolConfig.java          # ThreadPoolTaskExecutor & Rejection Handler
        │   ├── controller/
        │   │   └── TaskController.java            # REST & SSE stream endpoints
        │   ├── model/
        │   │   ├── PoolStats.java                 # Pool metrics (active, queue, rejected)
        │   │   └── TaskEvent.java                 # Real-time event payload
        │   └── service/
        │       ├── AsyncTaskService.java          # @Async background task with step progress
        │       └── SseBroadcaster.java            # Manages SSE connections and broadcasts
        └── resources/
            ├── application.properties             # Pool parameters configuration
            └── static/
                └── index.html                     # Interactive Vue 3 + Tailwind UI
```

