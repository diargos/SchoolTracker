package com.example.schooltracker;

public enum TrackerType {
    TERM,
    COURSE,
    ASSESSMENT,
    ALERT,
    NOTE,
    MENTOR;

    public TrackerType getChildType() {
        switch (this) {
            case TERM:
                return COURSE;
            case COURSE:
                return ASSESSMENT;
            case ASSESSMENT:
                return NOTE;
            default:
                return TERM;
        }
    }

    public static TrackerType getTypeFromString(String type) {
        switch (type.toUpperCase()) {
            case "TERM": return TERM;
            case "COURSE": return COURSE;
            case "ASSESSMENT": return ASSESSMENT;
            case "ALERT": return ALERT;
            case "NOTE": return NOTE;
            case "MENTOR": return MENTOR;
            default: return null;
        }
    }
}
