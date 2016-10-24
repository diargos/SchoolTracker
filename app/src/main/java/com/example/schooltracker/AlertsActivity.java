package com.example.schooltracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AlertsActivity extends AppCompatActivity {

    private ListView listView;
    private String selectedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Alerts");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);


        listView = new ListView(this);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setSelector(R.color.selector);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                cursor.moveToPosition(position);
                selectedID = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            }
        });

        layout.addView(listView);
        setContentView(layout, params);

        loadExistingData();
    }

    private void loadExistingData() {
        String filter = "type='ALERT'";
        String[] from = new String[]{"name"};
        int layout = android.R.layout.simple_list_item_1;
        int[] to = new int[]{android.R.id.text1};

        Cursor cursor = getContentResolver().query(STProvider.CONTENT_URI,
                DBOpenHelper.COLUMNS, filter, null, null);

        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                layout, cursor, from, to, 0);

        listView.setAdapter(cursorAdapter);
    }

    private void deleteData() {
        if (selectedID != null && !selectedID.isEmpty()) {
            String filter = "_id=" + selectedID;
            getContentResolver().delete(STProvider.CONTENT_URI, filter, null);
            Toast.makeText(this, "Alert deleted", Toast.LENGTH_SHORT).show();
            selectedID = null;
            loadExistingData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete:
                deleteData();
                break;
        }
        return true;
    }
}
