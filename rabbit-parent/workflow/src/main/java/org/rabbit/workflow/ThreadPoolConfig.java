package org.rabbit.workflow;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 公用线程池配置
 *
 * @author weltuser
 */
@Slf4j
@EnableAsync
@Configuration
public class ThreadPoolConfig {

    public static final String CREATE_FILE_MAIN_THREAD_POOL = "createFileMainThreadPool";

    public static final String CREATE_FILE_CHILD_THREAD_POOL = "createFileChildThreadPool";

    private static ExecutorService createFileMainTtlThreadPool = null;

    private static ExecutorService createFileChildTtlThreadPool = null;

    static {
        // 初始化创建文件主线程池
        ExecutorService createFileMainThreadPool = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));
        createFileMainTtlThreadPool = TtlExecutors.getTtlExecutorService(createFileMainThreadPool);
        // 初始化创建文件子线程池
        ExecutorService createFileChildThreadPool = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000));
        createFileChildTtlThreadPool = TtlExecutors.getTtlExecutorService(createFileChildThreadPool);
    }

    public static ExecutorService getCreateFileMainTtlThreadPool() {
        return createFileMainTtlThreadPool;
    }

    public static ExecutorService getCreateFileChildTtlThreadPool() {
        return createFileChildTtlThreadPool;
    }


}
