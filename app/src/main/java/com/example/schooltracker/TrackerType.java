package com.example.schooltracker;

public enum TrackerType {
    TERM,
    COURSE,
    ASSESSMENT,
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

    public static TrackerType getTypeFromString(String type) {
        switch (type.toUpperCase()) {
            case "TERM": return TERM;
            case "COURSE": return COURSE;
            case "ASSESSMENT": return ASSESSMENT;
            case "ALERT": return ALERT;
            case "NOTE": return NOTE;
            default: return null;
        }
    }
}
