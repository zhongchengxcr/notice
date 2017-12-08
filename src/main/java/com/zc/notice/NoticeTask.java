package com.zc.notice;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/08 下午9:56
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class NoticeTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static OkHttpClient okHttpClient;

    private final static String SUCCESS = "SUCCESS";

    private TimeDefinition timeDefinition;

    private String url;

    private Object body;

    private ScheduledExecutorService scheduledExecutorService;

    private boolean startNow = true;

    static {

        okHttpClient = new OkHttpClient.Builder()
                //.addInterceptor(new GzipRequestInterceptor())
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    public NoticeTask(TimeDefinition timeDefinition, String url, Object body) {
        this.timeDefinition = timeDefinition;
        this.url = url;
        this.body = body;

    }

    @Override
    public void run() {
        Long[] intervals = timeDefinition.getIntervals();

        Notice notice = new Notice(url, body);
        String futureGet = null;

        if (startNow) {
            try {
                logger.info("start now!");
                futureGet = scheduledExecutorService.submit(notice).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            logger.info("start now call back:{}", futureGet);
            if (!StringUtils.isEmpty(futureGet) && SUCCESS.equals(futureGet.trim().toUpperCase())) {

                return;
            }
        }

        for (Long interval : intervals) {

            Future<String> future = scheduledExecutorService.schedule(notice, interval, TimeUnit.SECONDS);

            try {
                String res = future.get();
                logger.info("Scheduler call http res:{}", res);
                if (!StringUtils.isEmpty(res) && SUCCESS.equals(res.trim().toUpperCase())) {
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Scheduler call http error!", e);
            }

        }


    }


    static class Notice implements Callable<String> {

        private Logger logger = LoggerFactory.getLogger(getClass());

        private String url;

        private Object body;

        private final static MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

        private final static int OK = 200;

        public Notice(String url, Object body) {
            this.url = url;
            this.body = body;
        }

        @Override
        public String call() throws Exception {

            RequestBody requestBody = RequestBody.create(JSON_TYPE, JSON.toJSONString(this.body));

            logger.info(JSON.toJSONString(this.body));

            Request request = new Request.Builder()
                    //尽可能服用底层TCP连接
                    .addHeader("Connection", "keep-alive")
                    .url(url)
                    .post(requestBody)
                    .build();

            Call call = okHttpClient.newCall(request);
            Response response;
            try {
                response = call.execute();
            } catch (IOException e) {
                return null;
            }

            if (response != null && response.code() == OK) {
                if (response.body() != null) {
                    return response.body().string();
                }
            }

            return null;
        }
    }


    public NoticeTask setTimeDefinition(TimeDefinition timeDefinition) {
        this.timeDefinition = timeDefinition;
        return this;
    }

    public NoticeTask setUrl(String url) {
        this.url = url;
        return this;
    }

    public NoticeTask setBody(Object body) {
        this.body = body;
        return this;
    }


    public NoticeTask setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        return this;
    }

    public NoticeTask setStartNow(boolean startNow) {
        this.startNow = startNow;
        return this;
    }
}
