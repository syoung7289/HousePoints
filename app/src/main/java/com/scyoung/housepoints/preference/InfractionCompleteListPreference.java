package com.scyoung.housepoints.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.scyoung.housepoints.util.HousePointsUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class InfractionCompleteListPreference extends MultiSelectListPreference {
    private SharedPreferences prefs;

    public InfractionCompleteListPreference(Context context, AttributeSet attributes) {
        super(context, attributes);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        setValues(new HashSet<String>());
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        HousePointsUtil.getActiveHousePoints(prefs);
        SortedMap<String, String> uniqueMap = new TreeMap<>();
        Map<String, ?> allEntries = prefs.getAll();
        SimpleDateFormat formatter = new SimpleDateFormat("M/d '@' h:mm a");
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().contains("~")) {
                String[] prefArray = entry.getKey().split("~");
                if (prefArray[3].equals("1")) {
                    Date logged = HousePointsUtil.parseDateString(prefArray[1]);
                    uniqueMap.put(prefArray[0] + "~" + prefArray[1] + "~" + prefArray[2],
                            "\n" + prefArray[2] + "\n\t \'" + prefArray[0] + "\' Logged: " + formatter.format(logged));
                }
            }
        }
        setEntries(uniqueMap.values().toArray(new CharSequence[]{}));
        setEntryValues(uniqueMap.keySet().toArray(new CharSequence[]{}));
        super.onPrepareDialogBuilder(builder);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            SharedPreferences.Editor editor = prefs.edit();
            Map<String, ?> allEntries = prefs.getAll();
            for (String categoryToRemove : this.getValues()) {
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    String key = entry.getKey();
                    if (key.contains(categoryToRemove)) {
                        editor.remove(key);
                    }
                }
            }
            editor.commit();
            setValues(new HashSet<String>());
        }
    }
}
