package com.zc.notice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/08 下午9:13
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class NoticeExecutor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 负责执行notice task
     */
    private ExecutorService noticeTaskExecutor;

    /**
     * 负责执行 notice task 中的调度task
     */
    private ScheduledExecutorService noticeScheduledExecutor;

    private final static String NOTICE_TASK_THREAD_NAME_FORMAT = "notice-task-thread-pool-%d";

    private final static String NOTICE_THREAD_NAME_FORMAT = "notice-scheduled-thread-pool-%d";

    private final AtomicLong noticeTaskCount = new AtomicLong();

    private final AtomicLong noticeScheduled = new AtomicLong();

    private final static int NOTICE_TASK_THREAD_NUM = 20;

    private final static int NOTICE_SCHEDULED_THREAD_NUM = 20;

    private NoticeExecutor() {

        ThreadFactory noticeTaskThreadFactory = (Runnable r) -> {
            Thread thread = new Thread(r);
            String name = String.format(NOTICE_TASK_THREAD_NAME_FORMAT, noticeTaskCount.addAndGet(1L));
            thread.setName(name);
            return thread;
        };

        ThreadFactory noticeScheduledThreadFactory = (Runnable r) -> {
            Thread thread = new Thread(r);
            String name = String.format(NOTICE_THREAD_NAME_FORMAT, noticeScheduled.addAndGet(1L));
            thread.setName(name);
            return thread;
        };

        noticeTaskExecutor = Executors.newFixedThreadPool(NOTICE_TASK_THREAD_NUM, noticeTaskThreadFactory);
        noticeScheduledExecutor = Executors.newScheduledThreadPool(NOTICE_SCHEDULED_THREAD_NUM, noticeScheduledThreadFactory);


    }

    private static NoticeExecutor noticeExecutor;


    public static synchronized NoticeExecutor getExecutor() {
        if (noticeExecutor == null) {
            noticeExecutor = new NoticeExecutor();
        }
        return noticeExecutor;
    }


    public void submit(NoticeTask noticeTask) {
        if (noticeTask != null) {
            logger.info("submit task ! ");
            noticeTask.setScheduledExecutorService(noticeScheduledExecutor);
            noticeTaskExecutor.execute(noticeTask);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        NoticeExecutor noticeExecutor = NoticeExecutor.getExecutor();
        TimeDefinition timeDefinition = new TimeDefinition();

        User user = new User()
                .setAge(1)
                .setName("zc");

        for (int i = 0; i < 99; i++) {
            NoticeTask noticeTask = new NoticeTask(timeDefinition, "http://localhost:8081/user", user);
            noticeExecutor.submit(noticeTask);
        }

    }



}
