package com.example.schooltracker;

public enum TrackerType {
    TERM,
    COURSE,
    ASSESSMENT,
    MENTOR,
    ALERT,
    NOTE;

    public TrackerType getChildType() {
        switch (this) {
            case TERM:
                return COURSE;
            case COURSE:
                return ASSESSMENT;
            default:
                return TERM;
        }
    }
}
