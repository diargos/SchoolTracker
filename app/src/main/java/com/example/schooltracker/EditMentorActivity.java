package com.example.schooltracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.schooltracker.Model.TrackerObject;

public class EditMentorActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editPhone;
    private EditText editEmail;
    private int id;
    private String action;
    private String filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Mentor");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        editName = new EditText(this);
        editName.setHint("Enter Name...");


        editPhone = new EditText(this);
        editPhone.setHint("Enter Phone Number...");


        editEmail = new EditText(this);
        editEmail.setHint("Enter Email...");

        layout.addView(editName);
        layout.addView(editPhone);
        layout.addView(editEmail);

        setContentView(layout, params);

        loadExistingData();
    }

    private void loadExistingData() {
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(STProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
        } else {
            action = Intent.ACTION_EDIT;
            filter = "_id=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.COLUMNS, filter, null, null);
            cursor.moveToFirst();

            this.id = cursor.getInt(cursor.getColumnIndex("_id"));
            editName.setText(cursor.getString(cursor.getColumnIndex("name")));
            editPhone.setText(cursor.getString(cursor.getColumnIndex("data1")));
            editEmail.setText(cursor.getString(cursor.getColumnIndex("data2")));

            cursor.close();
        }
    }

    @Override
    public void onBackPressed() {
        finishEditing();
        finish();
    }

    private void finishEditing() {
        TrackerObject newObj = new TrackerObject();
        newObj.id = this.id;
        newObj.name = editName.getText().toString().trim();
        newObj.type = TrackerType.MENTOR;
        newObj.data1 = editPhone.getText().toString();
        newObj.data2 = editEmail.getText().toString();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newObj.name.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertData(newObj);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newObj.name.length() == 0) {
                    deleteData();
                } else {
                    updateData(newObj);
                }
        }
    }

    private void updateData(TrackerObject obj) {
        // TODO: replace the hard-coded strings
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("type", obj.type.toString());
        values.put("parent", obj.parent);
        values.put("data1", obj.data1);
        values.put("data2", obj.data2);
        values.put("data3", obj.data3);
        values.put("data4", obj.data4);
        String filter = "_id = " + obj.id;

        getContentResolver().update(STProvider.CONTENT_URI, values, filter, null);
        // todo: param for toast messages
        Toast.makeText(this, "Mentor updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private String insertData(TrackerObject obj) {
        // TODO: replace the hard-coded strings
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("type", obj.type.toString());
        values.put("parent", obj.parent);
        values.put("data1", obj.data1);
        values.put("data2", obj.data2);
        values.put("data3", obj.data3);
        values.put("data4", obj.data4);
        Uri uri = getContentResolver().insert(STProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
        return uri.getLastPathSegment();
    }

    private void deleteData() {
        getContentResolver().delete(STProvider.CONTENT_URI, filter, null);
        Toast.makeText(this, "Mentor deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
