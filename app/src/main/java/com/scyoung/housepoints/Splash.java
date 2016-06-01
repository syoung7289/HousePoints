package com.scyoung.housepoints;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    private RelativeLayout[] durationContainers = new RelativeLayout[4];
    private SharedPreferences prefs;
    private static final int PASSCODE_RESULT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        flushPreviousTestData();
        generateTestData();

    }

    private void flushPreviousTestData() {
        SharedPreferences.Editor editor = prefs.edit();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String status = entry.getKey().split("~")[0];
            if (status.contains("ACTIVE") || status.contains("PARDON") || status.contains("MISSING")) {
                editor.remove(entry.getKey());
            }
        }
        editor.commit();
    }

    private void generateTestData() {
        String[] infractions = new String[] {"LYING", "DISOBEYING", "LYING"};
        int[] points = new int[] {1, 2, 2};
        int counter = 0;
        SharedPreferences.Editor editor = prefs.edit();
        for (int i=0; i < infractions.length; i++) {
            counter++;
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            catch(Exception e) {
                //dont care
            }
            String dateNow = HousePointsUtil.getFormatter().format(new Date());
            for (int j=1; j <= points[i]; j++) {
                String key = "ACTIVE~" + dateNow + "~This is infraction number " + counter + "~" + j;

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
        refreshStatuses(null);
    }

    public void refreshStatuses(View view) {
        initStatusIndicators();
        setStatusIndicators();
    }

    private void initStatusIndicators() {
        progressBars[0] = (ProgressBar)findViewById(R.id.dessertProgress);
        progressBars[0].setMax(Integer.parseInt(prefs.getString("dessert_threshold", "2")));
        statuses[0] = (ImageView)findViewById(R.id.dessertStatus);
        statuses[0].setActivated(false);
        durationContainers[0] = (RelativeLayout)findViewById(R.id.dessertDurationContainer);
        durationContainers[0].setVisibility(View.INVISIBLE);
        ((RelativeLayout)durationContainers[0].getParent()).setOnClickListener(null);
        progressBars[1] = (ProgressBar)findViewById(R.id.phoneProgress);
        progressBars[1].setMax(Integer.parseInt(prefs.getString("phone_threshold", "3")));
        statuses[1] = (ImageView)findViewById(R.id.phoneStatus);
        statuses[1].setActivated(false);
        durationContainers[1] = (RelativeLayout)findViewById(R.id.phoneDurationContainer);
        durationContainers[1].setVisibility(View.INVISIBLE);
        ((RelativeLayout)durationContainers[1].getParent()).setOnClickListener(null);
        progressBars[2] = (ProgressBar)findViewById(R.id.xBoxProgress);
        progressBars[2].setMax(Integer.parseInt(prefs.getString("xBox_threshold", "8")));
        statuses[2] = (ImageView)findViewById(R.id.xBoxStatus);
        statuses[2].setActivated(false);
        durationContainers[2] = (RelativeLayout)findViewById(R.id.xBoxDurationContainer);
        durationContainers[2].setVisibility(View.INVISIBLE);
        ((RelativeLayout)durationContainers[2].getParent()).setOnClickListener(null);
        progressBars[3] = (ProgressBar)findViewById(R.id.roomProgress);
        progressBars[3].setMax(prefs.getInt("roomProgress", 100));
        statuses[3] = (ImageView)findViewById(R.id.roomStatus);
        statuses[3].setActivated(false);
        durationContainers[3] = (RelativeLayout)findViewById(R.id.roomDurationContainer);
        durationContainers[3].setVisibility(View.INVISIBLE);
        ((RelativeLayout)durationContainers[3].getParent()).setOnClickListener(null);
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
            setDurationStatus(durationContainers[i], tierPoints, progressBars[i].getMax());
        }
    }

    public void showManagePoints(View view) {
        if (isPasscodeSet()) {
            Intent i = new Intent(this, PasscodeActivity.class);
            startActivityForResult(i, PASSCODE_RESULT);
        }
        else {
            Intent intent = new Intent(this, PointSettings.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (PASSCODE_RESULT) : {
                if (resultCode == Activity.RESULT_OK) {
                    boolean passcodeSuccess = data.getBooleanExtra(getResources().getString(R.string.passcode_success), false);
                    if (passcodeSuccess) {
                        Intent intent = new Intent(this, PointSettings.class);
                        startActivity(intent);
                    }
                }
                break;
            }
        }
    }

    public void setDurationStatus(RelativeLayout durationContainer, int tierPoints, int tierMax) {
        if (tierPoints > 0 && (tierPoints < tierMax || tierPoints == 100) && HousePointsUtil.nextPointCount > 0) {
            durationContainer.setVisibility(View.VISIBLE);
            String nextPointCount = String.valueOf(HousePointsUtil.nextPointCount);
            ((TextView) durationContainer.getChildAt(0)).setText(nextPointCount);
            String durationText = "";
            if (HousePointsUtil.nextPointHour > 0) {
                durationText = HousePointsUtil.nextPointHour + "hr";
                if (HousePointsUtil.nextPointHour != 1) {
                    durationText = durationText + "s ";
                }
            }
            if (HousePointsUtil.nextPointMinute == 0 && HousePointsUtil.nextPointHour == 0) {
                durationText = "<1 min";
            }
            else {
                durationText = durationText + HousePointsUtil.nextPointMinute + " min";
                if (HousePointsUtil.nextPointMinute != 1) {
                    durationText = durationText + "s";
                }
            }
            ((TextView) durationContainer.getChildAt(2)).setText(durationText);
            ((RelativeLayout)durationContainer.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshStatuses(v);
                }
            });
        }
    }

    private boolean isPasscodeSet() {
        boolean isSet = false;
        if (prefs != null) {
            isSet = !(prefs.getString("user_passcode", "")).isEmpty();
        }
        return isSet;
    }
}
