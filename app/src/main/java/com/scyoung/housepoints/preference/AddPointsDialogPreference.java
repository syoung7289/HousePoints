package com.scyoung.housepoints.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.scyoung.housepoints.R;
import com.scyoung.housepoints.util.HousePointsUtil;

import java.util.Date;

public class AddPointsDialogPreference extends DialogPreference {
    private SharedPreferences prefs;
    private Spinner infractionType;
    private Spinner numPointsSpinner;
    private EditText description;
    private Context context;

    public AddPointsDialogPreference(Context context, AttributeSet attributes) {
        super(context, attributes);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    @Override
    protected void onBindDialogView(View view) {
        infractionType = (Spinner)view.findViewById(R.id.infraction_type_spinner);
        numPointsSpinner = (Spinner)view.findViewById(R.id.num_points);
        description = (EditText) view.findViewById(R.id.point_description);
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            String now = HousePointsUtil.getFormatter().format(new Date());
            String pointDescription = description.getText().toString();
            int pointTypePos = infractionType.getSelectedItemPosition();
            String[] pointTypeVals = context.getResources().getStringArray(R.array.infraction_keys);
            int numPointsPos = numPointsSpinner.getSelectedItemPosition();
            String[] numPointVals = context.getResources().getStringArray(R.array.num_point_keys);
            int numPoints = Integer.valueOf(numPointVals[numPointsPos]);
            SharedPreferences.Editor editor = prefs.edit();
            for (int i=1; i<=numPoints; i++) {
                editor.putString("ACTIVE~" + now + "~" + pointDescription + "~" + i, pointTypeVals[pointTypePos]);
            }
            editor.commit();
        }
    }
}
