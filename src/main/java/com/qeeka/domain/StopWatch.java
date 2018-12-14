package com.qeeka.domain;

/**
 * Created by neal.xu on 2016/11/07.
 */
public class StopWatch {
    private long start;
    private final long createTime;

    public StopWatch() {
        start = createTime = System.currentTimeMillis();
    }

    public void reset() {
        start = System.currentTimeMillis();
    }

    public long elapsedTime() {
        long end = System.currentTimeMillis();
        return end - start;
    }

    public long elapsedAndReset() {
        long time = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        return time;
    }

    public long totalTime() {
        return System.currentTimeMillis() - createTime;
    }
}
