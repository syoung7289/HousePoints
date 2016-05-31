package com.scyoung.housepoints.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.AttributeSet;


public class PasscodeSwitchPreference extends SwitchPreference {
    private SharedPreferences prefs;

    public PasscodeSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean isChecked() {
        return isPasscodeSet();
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked && isPasscodeSet()) {
            super.setChecked(checked);
        }
        else if (!checked && !isPasscodeSet()) {
            super.setChecked(checked);
        }
    }

    private boolean isPasscodeSet() {
        boolean isSet = false;
        if (prefs != null) {
            isSet = !(prefs.getString("user_passcode", "")).isEmpty();
        }
        return isSet;
    }

    public void markPasscodeSuccess() {
        setChecked(isPasscodeSet());
    }
}
