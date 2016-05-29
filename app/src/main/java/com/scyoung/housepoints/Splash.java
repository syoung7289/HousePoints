package com.scyoung.housepoints;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.scyoung.housepoints.util.HousePointsUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Splash extends AppCompatActivity {

    private ProgressBar[] progressBars = new ProgressBar[4];
    private ImageView[] statuses = new ImageView[4];
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //flushPreviousTestData();
        generateTestData();

    }

    private void flushPreviousTestData() {
        SharedPreferences.Editor editor = prefs.edit();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().split("~")[0].contains("ACTIVE")) {
                editor.remove(entry.getKey());
            }
        }
        editor.commit();
    }

    private void generateTestData() {
        String[] infractions = new String[] {"LYING", "DISOBEYING", "LYING"};
        int[] points = new int[] {1, 2, 2};
        SharedPreferences.Editor editor = prefs.edit();
        for (int i=0; i < infractions.length; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            catch(Exception e) {
                //dont care
            }
            String dateNow = HousePointsUtil.getFormatter().format(new Date());
            for (int j=1; j <= points[i]; j++) {
                String key = "ACTIVE~This is infraction number " + j + "~" + dateNow + "~" + j;
                editor.putString(key, infractions[i]);
            }
        }
        editor.commit();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initStatusIndicators();
        setStatusIndicators();
    }

    private void initStatusIndicators() {
        progressBars[0] = (ProgressBar)findViewById(R.id.dessertProgress);
        progressBars[0].setMax(Integer.parseInt(prefs.getString("dessert_threshold", "2")));
        statuses[0] = (ImageView)findViewById(R.id.dessertStatus);
        statuses[0].setActivated(false);
        progressBars[1] = (ProgressBar)findViewById(R.id.phoneProgress);
        progressBars[1].setMax(Integer.parseInt(prefs.getString("phone_threshold", "3")));
        statuses[1] = (ImageView)findViewById(R.id.phoneStatus);
        statuses[1].setActivated(false);
        progressBars[2] = (ProgressBar)findViewById(R.id.xBoxProgress);
        progressBars[2].setMax(Integer.parseInt(prefs.getString("xBox_threshold", "8")));
        statuses[2] = (ImageView)findViewById(R.id.xBoxStatus);
        statuses[2].setActivated(false);
        progressBars[3] = (ProgressBar)findViewById(R.id.roomProgress);
        progressBars[3].setMax(prefs.getInt("roomProgress", 100));
        statuses[3] = (ImageView)findViewById(R.id.roomStatus);
        statuses[3].setActivated(false);
    }

    private void setStatusIndicators() {
        int housePoints = HousePointsUtil.getActiveHousePoints(prefs);
        Log.d("housePoints", String.valueOf(housePoints));
        Toast.makeText(this, "Current House Points: " + housePoints, Toast.LENGTH_LONG).show();
        for (int i=0; i<progressBars.length; i++) {
            int tierPoints = Math.min(housePoints, progressBars[i].getMax());
            progressBars[i].setProgress(tierPoints);
            housePoints -= tierPoints;
            statuses[i].setActivated(tierPoints > 0);
        }
    }

    public void showManagePoints(View view) {
        Intent intent = new Intent(this, PointSettings.class);
        startActivity(intent);
    }
}
