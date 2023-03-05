package com.example.chronometer;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.Date;

public class ChronometerData {
    public static final int CHRONOMETER_STATE_RESET = 0;
    public static final int CHRONOMETER_STATE_STOPPED = 1;
    public static final int CHRONOMETER_STATE_PAUSED = 2;
    public static final int CHRONOMETER_STATE_RUNNING = 3;

    @SuppressLint("SimpleDateFormat")
    public static DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SS");

    public static String format(Date d) {
        return dateFormatter.format(d);
    }

    private int status;
    private ArrayList<Date> laps;
    private Date lastStartTime;
    private long offset;

    public void reset() {
        status = CHRONOMETER_STATE_RESET;
        laps = new ArrayList<>();
        lastStartTime = null;
        offset = 0L;
    }

    public ChronometerData() {
        reset();
    }

    public int getStatus() {
        return status;
    }

    public void start() {
        if (status == CHRONOMETER_STATE_RUNNING)
            throw new RuntimeException("Chronometer already running");
        if (status == CHRONOMETER_STATE_STOPPED)
            reset();
        lastStartTime = new Date();
        status = CHRONOMETER_STATE_RUNNING;
    }

    public void stop() {
        if (status <= CHRONOMETER_STATE_STOPPED)
            throw new RuntimeException("Chronometer already stopped");
        status = CHRONOMETER_STATE_STOPPED;
        offset = getValue().getTime();
    }

    public void pause() {
        if (status != CHRONOMETER_STATE_RUNNING)
            throw new RuntimeException("Chronometer not running");
        offset = getValue().getTime();
        status = CHRONOMETER_STATE_PAUSED;
    }

    public Date getValue() {
        if (status != CHRONOMETER_STATE_RUNNING)
            return new Date(offset);
        Date curTime = new Date();
        long value = curTime.getTime() - lastStartTime.getTime();
        value += offset;
        return new Date(value);
    }

    public String getFormattedValue() {
        return dateFormatter.format(getValue());
    }

    public void addLap() {
        if (status != CHRONOMETER_STATE_RUNNING)
            throw new RuntimeException("Chronometer not running");
        laps.add(getValue());
    }

    public boolean hasLaps() {
        return laps.size() > 0;
    }

    public Date getLastLapTime() {
        if (!hasLaps())
            return new Date(0);
        return laps.get(laps.size() - 1);
    }
}
