package com.example.chronometer;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.chronometer.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView counter;
    private TextView lastLapAbsolute;
    private Button startPause;
    private Button lapReset;
    private Button viewLaps;

    private Handler handler;

    private UpdateStopwatch runnable;
    private static final long UPDATE_INTERVAL = 42L;
    private ChronometerData chronometer;


    private class UpdateStopwatch implements Runnable {
        @Override
        public void run() {
            if (MainActivity.this.
                    chronometer.getStatus() == ChronometerData.CHRONOMETER_STATE_RUNNING) {
                MainActivity.this.updateTime();
                MainActivity.this.handler.postDelayed(this, MainActivity.UPDATE_INTERVAL);
            }
        }
    }

    private void updateTime() {
        counter.setText(chronometer.getFormattedValue());
    }

    private void updateLapUI() {
        if (!chronometer.hasLaps()) {
            lastLapAbsolute.setVisibility(View.INVISIBLE);
            viewLaps.setVisibility(View.INVISIBLE);
            return;
        }
        String lastLapTime = chronometer.getFormattedValue();
        lastLapAbsolute.setText(String.format("%s %s", getString(R.string.last_lap_base), lastLapTime));
        lastLapAbsolute.setVisibility(View.VISIBLE);
        viewLaps.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        switch (chronometer.getStatus()) {
            case ChronometerData.CHRONOMETER_STATE_RESET:
                startPause.setText(getString(R.string.start));
                lapReset.setVisibility(View.GONE);
                break;
            case ChronometerData.CHRONOMETER_STATE_STOPPED:
                startPause.setText(getString(R.string.start));
                lapReset.setVisibility(View.VISIBLE);
                lapReset.setText(getString(R.string.reset));
                break;
            case ChronometerData.CHRONOMETER_STATE_PAUSED:
                startPause.setText(getString(R.string.start));
                lapReset.setText(getString(R.string.stop));
                break;
            case ChronometerData.CHRONOMETER_STATE_RUNNING:
                startPause.setText(getString(R.string.pause));
                lapReset.setVisibility(View.VISIBLE);
                lapReset.setText(getString(R.string.lap));
                break;
        }
        updateTime();
        updateLapUI();
    }

    private void onStartPauseClick() {
        switch (chronometer.getStatus()) {
            case ChronometerData.CHRONOMETER_STATE_RUNNING:
                handler.removeCallbacks(runnable);
                chronometer.pause();
                updateTime();
                break;
            case ChronometerData.CHRONOMETER_STATE_RESET:
            case ChronometerData.CHRONOMETER_STATE_STOPPED:
            case ChronometerData.CHRONOMETER_STATE_PAUSED:
                chronometer.start();
                handler.postDelayed(runnable, UPDATE_INTERVAL);
                break;
        }
        updateUI();
    }

    private void onLapResetClick() {
        switch (chronometer.getStatus()) {
            case ChronometerData.CHRONOMETER_STATE_RUNNING:
                chronometer.addLap();
                break;
            case ChronometerData.CHRONOMETER_STATE_PAUSED:
                chronometer.stop();
                break;
            case ChronometerData.CHRONOMETER_STATE_STOPPED:
                chronometer.reset();
                break;
        }
        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chronometer = new ChronometerData();

        counter = (TextView) findViewById(R.id.counterView);
        lastLapAbsolute = (TextView) findViewById(R.id.last_lap);
        startPause = (Button) findViewById(R.id.start_pause);
        lapReset = (Button) findViewById(R.id.lap_reset);
        viewLaps = (Button) findViewById(R.id.view_laps);

        counter.setText(R.string.base_value);
        startPause.setOnClickListener(this);
        lapReset.setOnClickListener(this);
        viewLaps.setOnClickListener(this);
        handler = new Handler();
        runnable = new UpdateStopwatch();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_pause)
            onStartPauseClick();
        else if (v.getId() == R.id.lap_reset)
            onLapResetClick();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (chronometer.getStatus() == ChronometerData.CHRONOMETER_STATE_RUNNING)
            handler.removeCallbacks(runnable);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (chronometer.getStatus() == ChronometerData.CHRONOMETER_STATE_RUNNING)
            handler.postDelayed(runnable, UPDATE_INTERVAL);
        updateUI();
    }
}