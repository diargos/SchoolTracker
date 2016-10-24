package com.example.schooltracker;

public class MentorsActivity extends  TrackerActivity {

    public MentorsActivity() {
        type = TrackerType.MENTOR;
        isDetailActivity = false;
        detailClass = EditMentorActivity.class;
    }
}
