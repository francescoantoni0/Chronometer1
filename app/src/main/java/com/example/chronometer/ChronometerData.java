package com.example.chronometer;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Stack;
import java.util.Date;

public class ChronometerData {
    public static final int CHRONOMETER_STATE_RESET = 0;
    public static final int CHRONOMETER_STATE_STOPPED = 1;
    public static final int CHRONOMETER_STATE_PAUSED = 2;
    public static final int CHRONOMETER_STATE_RUNNING = 3;

    public static DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SS", Locale.getDefault());

    public LocalDateTime getLastStartTime1() {
        return lastStartTime1;
    }

    private int status;
    private ArrayList<LocalDateTime> laps;
    private Date lastStartTime;

    private LocalDateTime lastStartTime1;
    private long offset;

    public void reset() {
        status = CHRONOMETER_STATE_RESET;
        laps = new ArrayList<>();
        lastStartTime1 = null;
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
        lastStartTime1 = LocalDateTime.now();
        status = CHRONOMETER_STATE_RUNNING;
    }

    public void stop() {
        if (status <= CHRONOMETER_STATE_STOPPED)
            throw new RuntimeException("Chronometer already stopped");
        status = CHRONOMETER_STATE_STOPPED;
        offset = Instant.from(getValue()).toEpochMilli();
    }

    public void pause() {
        if (status != CHRONOMETER_STATE_RUNNING)
            throw new RuntimeException("Chronometer not running");
        offset = Instant.from(getValue()).toEpochMilli();
        status = CHRONOMETER_STATE_PAUSED;
    }

    public LocalDateTime getValue() {
        if (status != CHRONOMETER_STATE_RUNNING)
            return Instant.ofEpochMilli(offset).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime curTime = LocalDateTime.now();
        long value = Instant.from(curTime).toEpochMilli() -
                Instant.from(lastStartTime1).toEpochMilli();
        value += offset;
        return Instant.ofEpochMilli(offset).atZone(ZoneId.systemDefault()).toLocalDateTime();
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

    public LocalDateTime getLastLapTime() {
        if (!hasLaps())
            return Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return laps.get(laps.size() - 1);
    }
}
