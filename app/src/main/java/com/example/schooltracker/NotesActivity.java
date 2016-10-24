package com.example.schooltracker;

public class NotesActivity extends TrackerActivity {

    public NotesActivity() {
        type = TrackerType.NOTE;
        isDetailActivity = false;
        detailClass = EditNoteActivity.class;
    }
}
