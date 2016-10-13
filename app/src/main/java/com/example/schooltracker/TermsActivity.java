package com.example.schooltracker;

public class TermsActivity extends TrackerActivity {

    public TermsActivity() {
        type = TrackerType.TERM;
        isDetailActivity = false;
        detailClass = EditTermActivity.class;
    }
}
