package com.example.schooltracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener
{
    private int type;
    public static final int DATA1_DATE_TYPE = 0;
    public static final int DATA2_DATE_TYPE = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Bundle bundle = getArguments();
        type = bundle.getInt("type");

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String date = (month+1) + "/" + day + "/" + year;

        switch (type) {
            case DATA1_DATE_TYPE:
                ((TrackerActivity)getActivity()).setData1(date);
                break;
            case DATA2_DATE_TYPE:
                ((TrackerActivity)getActivity()).setData2(date);
                break;
        }
    }
}
