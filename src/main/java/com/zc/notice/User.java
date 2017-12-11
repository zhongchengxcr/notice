package com.zc.notice;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/08 下午11:32
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class User {

    private String id;

    private String name;

    private int age;

    private int height;


    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public User setHeight(int height) {
        this.height = height;
        return this;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        FutureTask a = new FutureTask(()-> System.out.printf(""));
        a.get();

        Thread d = new Thread(a);

     }
}
