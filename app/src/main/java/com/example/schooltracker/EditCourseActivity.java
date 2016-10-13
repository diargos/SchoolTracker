package com.example.schooltracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

public class EditCourseActivity extends TrackerActivity {

    public EditCourseActivity() {
        type = TrackerType.COURSE;
        isDetailActivity = true;
        childClass = AssessmentsActivity.class;
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
