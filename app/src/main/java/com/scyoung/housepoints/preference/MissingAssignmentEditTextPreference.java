package com.scyoung.housepoints.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.scyoung.housepoints.util.HousePointsUtil;

import java.util.Date;

public class MissingAssignmentEditTextPreference extends EditTextPreference {
    private SharedPreferences prefs;

    public MissingAssignmentEditTextPreference(Context context, AttributeSet attributes) {
        super(context, attributes);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(getKey(), "").commit();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            String now = HousePointsUtil.getFormatter().format(new Date());
            String assignmentDescription = this.getText();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("MISSING~" + now, assignmentDescription);
            editor.putString(getKey(), "");
            editor.commit();

            this.resetElementValue(getKey());
        }
    }

    private void resetElementValue(String key) {
        EditTextPreference myPrefText = (EditTextPreference) super.findPreferenceInHierarchy(key);
        myPrefText.setText("");
    }

}
