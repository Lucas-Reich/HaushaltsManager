package com.example.lucas.haushaltsmanager.Entities;

import java.util.concurrent.TimeUnit;

public class Delay {
    // Kann bei bedarf durch Duration ausgetauscht werden
    private TimeUnit mTimeUnit;
    private long mDuration;

    public Delay(TimeUnit timeUnit, long duration) {
        mTimeUnit = timeUnit;
        mDuration = duration;
    }

    public TimeUnit getTimeUnit() {
        return mTimeUnit;
    }

    public long getDuration() {
        return mDuration;
    }
}
