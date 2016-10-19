package com.example.schooltracker;

public class AssessmentsActivity extends TrackerActivity {

    public AssessmentsActivity () {
        type = TrackerType.ASSESSMENT;
        isDetailActivity = false;
        detailClass = EditAssessmentActivity.class;
    }

}
