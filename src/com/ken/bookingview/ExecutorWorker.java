package com.ken.bookingview;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.SystemClock;
import android.util.Log;

public class ExecutorWorker {

    private static final String TAG = ExecutorWorker.class.getSimpleName();

    private static final int EXECUTOR_CORE_THREAD_COUNT = 4;
    private static final int EXECUTOR_MAX_THREAD_COUNT = 8;
    private static final int EXECUTOR_ALIVE_TIME_SEC = 60;

    private static final ThreadFactory sWorkerThreadFactory = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ExecutorWorker #" + mCount.getAndIncrement());
        }
    };

    private static final ThreadPoolExecutor sExecutorWorker = new ThreadPoolExecutor(EXECUTOR_CORE_THREAD_COUNT, EXECUTOR_MAX_THREAD_COUNT,
            EXECUTOR_ALIVE_TIME_SEC, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), sWorkerThreadFactory);

    public static final class ExecutorTask extends FutureTask<Void> {

        private static final String TAG = ExecutorTask.class.getSimpleName();

        private String mCallFrom;
        private long mExecuteTime;

        public ExecutorTask(Callable<Void> callable, String callFrom) {
            super(callable);
            mCallFrom = callFrom;
        }
        @Override
        public void run() {
            Log.i(TAG, String.format("[run] threadName: %s, from: [%s]", Thread.currentThread().getName(), mCallFrom));
            mExecuteTime = SystemClock.elapsedRealtime();
            super.run();
        }
        @Override
        protected void done() {
            final long executeTime = SystemClock.elapsedRealtime() - mExecuteTime;
            Log.i(TAG, String.format("[done] threadNamea: %s, executeTime: %d", Thread.currentThread().getName(), executeTime));
        }
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            Log.i(TAG, String.format("[cancel] threadName: %s, interruptIfRunning: %b", Thread.currentThread().getName(), mayInterruptIfRunning));
            return super.cancel(mayInterruptIfRunning);
        }
    }

    public static void execute(ExecutorTask task) {
        sExecutorWorker.execute(task);
    }

    public static void execute(List<ExecutorTask> tasks) {
        if (tasks == null) {
            Log.i(TAG, String.format("[execute] tasks must not be null"));
            return;
        }
        for (ExecutorTask task : tasks) {
            sExecutorWorker.execute(task);
        }
    }

    public static void cancel(Runnable r) {
        sExecutorWorker.remove(r);
    }

    public static void cancel() {
        BlockingQueue<Runnable> commands = sExecutorWorker.getQueue();
        for (Runnable r : commands) {
            sExecutorWorker.remove(r);
        }
    }
}
