package com.example.schooltracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

    public void onData3Click(View view) {
        String[] values = new String[]{"Plan to Take", "In Progress", "Completed", "Dropped"};

        TextView textView = (TextView) findViewById(R.id.tvData3);
        String currentValue = (String) textView.getText();
        String[] sa = currentValue.split(":");
        currentValue = sa[1].trim();

        for (int i = 0; i < values.length; i++) {
            if (currentValue.equals(values[i])) {
                if (i < values.length - 1)
                    currentValue = values[i + 1];
                else
                    currentValue = values[0];
                break;
            }
        }
        textView.setText("Status: " + currentValue);
    }

    public void showDatePickerDialog(View view, Bundle bundle) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(), "Date Picker");
    }
}
