package com.example.chronometer;


import java.time.Instant;
import java.time.ZoneId;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@SuppressWarnings({"unused", "inverted"})
public class ChronometerData {
    public static final int CHRONOMETER_STATE_RESET = 0;
    public static final int CHRONOMETER_STATE_STOPPED = 1;
    public static final int CHRONOMETER_STATE_PAUSED = 2;
    public static final int CHRONOMETER_STATE_RUNNING = 3;

    private final String dTz = "GMT+0";

    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS");

    public ZonedDateTime getLastStartTime() {
        return lastStartTime;
    }

    private int status;
    private ArrayList<ZonedDateTime> laps;

    private ZonedDateTime lastStartTime;
    private long offset;

    public void reset() {
        status = CHRONOMETER_STATE_RESET;
        laps = new ArrayList<>();
        lastStartTime = Instant.EPOCH.atZone(ZoneId.of(dTz));
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
        lastStartTime = ZonedDateTime.now();
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

    public ZonedDateTime getValue() {
        if (status != CHRONOMETER_STATE_RUNNING)
            return Instant.ofEpochMilli(offset).atZone(ZoneId.of(dTz));
        ZonedDateTime curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of(dTz));
        long value = curTime.toInstant().toEpochMilli() - lastStartTime.toInstant().toEpochMilli();
        value += offset;
        return Instant.ofEpochMilli(value).atZone(ZoneId.of(dTz));
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
        return (laps.size() > 0);
    }

    public ZonedDateTime getLastLapTime() {
        if (!hasLaps())
            return Instant.EPOCH.atZone(ZoneId.of(dTz));
        return laps.get(laps.size() - 1);
    }
}
