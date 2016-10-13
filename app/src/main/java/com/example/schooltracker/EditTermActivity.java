package com.example.schooltracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

public class EditTermActivity extends TrackerActivity
{
    public EditTermActivity() {
        type = TrackerType.TERM;
        isDetailActivity = true;
        childClass = CoursesActivity.class;
    }

    public void onData1Click(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("type",DatePickerFragment.DATA1_DATE_TYPE);
        showDatePickerDialog(view, bundle);
    }

    public void onData2Click(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("type",DatePickerFragment.DATA2_DATE_TYPE);
        showDatePickerDialog(view, bundle);
    }

    public void showDatePickerDialog(View view, Bundle bundle) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(), "Date Picker");
    }



}
