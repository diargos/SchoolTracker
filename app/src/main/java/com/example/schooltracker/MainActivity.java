package com.example.schooltracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openTermsScreen(View view) {
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
    }

    public void openCoursesScreen(View view) {
        Intent intent = new Intent(this, CoursesActivity.class);
        startActivity(intent);
    }

    public void openAssessmentsScreen(View view) {
        Intent intent = new Intent(this, AssessmentsActivity.class);
        startActivity(intent);
    }

    public void openAlertsScreen(View view) {
        Intent intent = new Intent(this, AlertsActivity.class);
        startActivity(intent);
    }

    public void openMentorsScreen(View view) {
        Intent intent = new Intent(this, MentorsActivity.class);
        startActivity(intent);
    }

    public void openNotesScreen(View view) {
        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);
    }
}
