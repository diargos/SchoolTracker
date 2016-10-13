package com.example.schooltracker;

public class CoursesActivity extends TrackerActivity {

    public CoursesActivity () {
        type = TrackerType.COURSE;
        isDetailActivity = false;
        detailClass = EditCourseActivity.class;
    }
}
