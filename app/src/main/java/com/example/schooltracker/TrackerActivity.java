package com.example.schooltracker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schooltracker.Model.TrackerObject;

public abstract class TrackerActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>
{
    protected TrackerType type;
    protected TrackerType childType;
    protected boolean isDetailActivity;
    protected Class<?> detailClass;
    protected Class<?> childClass;
    protected Uri contentURI;

    protected SimpleCursorAdapter cursorAdapter;
    protected SimpleCursorAdapter cursorAdapterChildren;
    protected String filter;
    protected String[] filterValues = new String[]{"",""};
    protected String action;
    protected TrackerObject existingData;
    protected String selectedFromList;

    protected EditText editorName;
    protected TextView editorData1;
    protected TextView editorData2;
    protected TextView editorData3;
    protected ListView editorChildList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isDetailActivity) {
            setContentView(R.layout.activity_detail);
        } else {
            setContentView(R.layout.activity_summary);
        }
        contentURI = STProvider.CONTENT_URI;
        childType = type.getChildType();
        filterValues[0] = type.toString();

        initCursorAdapter();
        initControls();
        initData();

        if (!isDetailActivity) {
            getLoaderManager().initLoader(0, null, this); // Summary

        } else {
            getLoaderManager().initLoader(1, null, this); // Detail
        }
    }

    //region Initializers

    private void initCursorAdapter() {
        if (!isDetailActivity) {
            int layout = android.R.layout.simple_list_item_1;
            int[] to = new int[]{android.R.id.text1};
            String[] from = new String[]{"name"};
            filter = "type = ?";
            filterValues = new String[]{type.toString()};
            cursorAdapter = new SimpleCursorAdapter(this, layout, null, from, to, 0);
        }
    }

    public void initControls() {
        if (isDetailActivity) {
            editorName = (EditText) findViewById(R.id.editName);
            editorData1 = (TextView) findViewById(R.id.tvData1);
            editorData2 = (TextView) findViewById(R.id.tvData2);
            editorData3 = (TextView) findViewById(R.id.tvData3);
            editorChildList = (ListView) findViewById(R.id.listChildren);
            Button editorAddChildBtn = (Button) findViewById(R.id.btnAddChild);
            Button editorRemoveChildBtn = (Button) findViewById(R.id.btnRemoveChild);
            TextView editorChildrenLbl = (TextView) findViewById(R.id.lblChildren);

            String[] labels;
            editorData3.setVisibility(View.GONE);

            switch (type) {
                case TERM:
                    labels = new String[]{"Term name",
                            "Start Date:     ",
                            "End Date:     ",
                            "Course"};
                    break;
                case COURSE:
                    labels = new String[]{"Course name",
                            "Start Date:     ",
                            "End Date:     ",
                            "Assessment"};
                    editorData3.setVisibility(View.VISIBLE);
                    editorData3.setText("Status: Plan to Take");
                    break;
                case ASSESSMENT:
                    labels = new String[]{"Assessment name",
                            "Due Date:     ",
                            "",
                            "Note"};
                    break;
                case NOTE:
                    labels = new String[]{"Note title",
                            "Add text",
                            "",
                            ""};
                    break;
                case ALERT:
                    labels = new String[]{"Alert title",
                            "Alert Date:     ",
                            "",
                            "Assessment"};
                    break;
                default:
                    labels = new String[]{"Name",
                            "Data1",
                            "Data2",
                            "ChildObject"};
            }


            editorName.setHint(labels[0]);
            editorData1.setText(labels[1]);
            editorData2.setText(labels[2]);
            editorAddChildBtn.setText("Add " + labels[3]);
            editorRemoveChildBtn.setText("Remove " + labels[3]);
            editorChildrenLbl.setText(labels[3] + "s");
        }
    }

    public void initData() {
        existingData = new TrackerObject();

        if (isDetailActivity) {
            Intent intent = getIntent();
            // todo: replace extras with static types, like this one
            Uri uri = intent.getParcelableExtra(STProvider.CONTENT_ITEM_TYPE);

            cursorAdapterChildren = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    null,
                    new String[]{"name","_id"},
                    new int[]{android.R.id.text1}, 0);

            editorChildList.setAdapter(cursorAdapterChildren);

            editorChildList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Cursor cursor = (Cursor) editorChildList.getItemAtPosition(position);
                    cursor.moveToPosition(position);
                    selectedFromList = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
                    System.out.println("On selection - object: " + cursor.getString(cursor.getColumnIndex("name")));
                    System.out.println("On selection - position: " + position);
                    System.out.println("On selection - id: " + id);
                }
            });

            if (uri == null) {
                action = intent.ACTION_INSERT;
                setTitle("New " + getType());
            } else {
                action = intent.ACTION_EDIT;
                setTitle("Edit " + getType());

                // Load existing data for the selected item
                filter = "type = ? AND _id = ?";
                filterValues = new String[]{type.toString(), uri.getLastPathSegment()};

                Cursor cursor = getContentResolver().query(uri,
                        DBOpenHelper.COLUMNS, filter, filterValues, null);
                cursor.moveToFirst();

                // todo: existing data, is this used elsewhere? collapse this if not
                existingData.id = cursor.getInt(cursor.getColumnIndex("_id"));
                existingData.name = cursor.getString(cursor.getColumnIndex("name"));
                existingData.parent = cursor.getInt(cursor.getColumnIndex("parent"));
                existingData.data1 = cursor.getString(cursor.getColumnIndex("data1"));
                existingData.data2 = cursor.getString(cursor.getColumnIndex("data2"));
                existingData.data3 = cursor.getString(cursor.getColumnIndex("data3"));

                editorName.setText(existingData.name);
                editorName.requestFocus();
                editorData1.setText(existingData.data1);
                editorData2.setText(existingData.data2);
                editorData3.setText(existingData.data3);
            }
        } else {  // summary activity
            ListView list = (ListView) findViewById(android.R.id.list);
            list.setAdapter(cursorAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    boolean addChild = getIntent().getBooleanExtra("AddChild", false);

                    if (addChild) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("ChildToAdd", String.valueOf(id));
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Intent intent = new Intent(TrackerActivity.this, detailClass);
                        // todo: STProvider.CONTENT_URI only... is this even needed if using filter on id?
                        // todo: content_item_type
                        Uri uri = Uri.parse(STProvider.CONTENT_URI + "/" + id); // todo: make this uri static somewhere
                        intent.putExtra(STProvider.CONTENT_ITEM_TYPE, uri);
                        startActivityForResult(intent, TrackerObject.NORMAL_REQUEST);
                    }
                }
            });
        }
    }

    //endregion

    public void setData1(String data) {
        TextView tv = (TextView) findViewById(R.id.tvData1);
        String desc;
        switch(type) {
            case TERM:
            case COURSE:
                desc = "Start date: ";
                break;
            case ASSESSMENT:
                desc = "Due date: ";
                break;
            default:
                desc = "";
        }
        tv.setText(desc + data);
    }

    public void setData2(String data) {
        TextView tv = (TextView) findViewById(R.id.tvData2);
        String desc;
        switch(type) {
            case TERM:
            case COURSE:
                desc = "End date: ";
                break;
            default:
                desc = "";
        }
        tv.setText(desc + data);
    }

    private String getType() {
        String s = type.toString();
        s = s.substring(0,1) + s.substring(1).toLowerCase();
        return s;
    }

    public void createNewObject(View view) {
        Intent intent = new Intent(this, detailClass);
        startActivityForResult(intent, TrackerObject.NORMAL_REQUEST);
    }

    public void addChild(View view) {
        if (action == Intent.ACTION_INSERT) {
            filterValues[1] = finishEditing();
        }
        editorChildList.setAdapter(cursorAdapterChildren);
        Intent intent = new Intent(this, childClass);
        intent.putExtra("AddChild", true);
        startActivityForResult(intent, TrackerObject.ADD_CHILD_REQUEST);
    }

    public void removeChild(View view) {
        if (selectedFromList != null) {
            TrackerObject obj = retrieveData(selectedFromList);
            obj.parent = 0;
            updateData(obj);
            selectedFromList = null;
            editorChildList.setAdapter(cursorAdapterChildren);
            restartLoader(1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO: sort this mess out...
        if (requestCode == TrackerObject.NORMAL_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (!isDetailActivity) {
                    restartLoader(0); // Summary
                } else {
                    restartLoader(1); // Detail
                }
            }
        } else if (requestCode == TrackerObject.ADD_CHILD_REQUEST) {
            if (resultCode == RESULT_OK) {
                String childToAdd = data.getStringExtra("ChildToAdd");
                TrackerObject child = retrieveData(childToAdd);
                child.parent = Integer.parseInt(filterValues[1]);
                updateData(child);
                restartLoader(1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isDetailActivity && action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isDetailActivity) {
            finishEditing();
            finish();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isDetailActivity) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finishEditing();
                    finish();
                    break;
                case R.id.action_delete:
                    if (type == TrackerType.TERM) {
                        deleteTerm();
                    } else {
                        deleteData();
                    }
                    break;
            }
        }
        return true;
    }

    private void deleteTerm() {
        Cursor cursor = getContentResolver().query(STProvider.CONTENT_URI,
                DBOpenHelper.COLUMNS, "parent=" + filterValues[1], null, null);

        if (cursor == null || cursor.getCount() == 0) {
            deleteData();
        } else {
            Toast.makeText(this, "Terms with assigned courses cannot be deleted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected String finishEditing() {
        TrackerObject newObj = new TrackerObject();
        newObj.id = Integer.valueOf(filterValues[1] == "" ? "0" : filterValues[1]);
        newObj.name = editorName.getText().toString().trim();
        newObj.type = this.type;
        newObj.data1 = editorData1.getText().toString();
        newObj.data2 = editorData2.getText().toString();
        newObj.data3 = editorData3.getText().toString();
        String result = "";

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newObj.name.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    result = insertData(newObj);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newObj.name.length() == 0) {
                    if (type == TrackerType.TERM) {
                        deleteTerm();
                    } else {
                        deleteData();
                    }
                } else {
                    updateData(newObj);
                }
        }
        action = Intent.ACTION_EDIT;
        return result;
    }

    //region LoaderManager

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case 0:
                return new CursorLoader(this, contentURI, DBOpenHelper.COLUMNS, filter, filterValues, null);
            case 1:
                String fc = "type=? AND parent=?";
                String[] fcv = new String[]{childType.toString(),filterValues[1]};
                return new CursorLoader(this, contentURI, DBOpenHelper.COLUMNS, fc, fcv, null);
            default:
                return new CursorLoader(this, contentURI, DBOpenHelper.COLUMNS, filter, filterValues, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case 0: // summary
                cursorAdapter.swapCursor(cursor);
                break;
            case 1: // detail children
                cursorAdapterChildren.swapCursor(cursor);
                break;
            default:
                cursorAdapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case 0:
                cursorAdapter.swapCursor(null);
                break;
            case 1:
                cursorAdapterChildren.swapCursor(null);
                break;
            default:
                cursorAdapter.swapCursor(null);
                break;
        }
    }

    private void restartLoader(int loader) {
        getLoaderManager().restartLoader(loader, null, this);
    }
    //endregion

    //region Data Handling

    protected TrackerObject retrieveData(String id) {
        TrackerObject obj = new TrackerObject();
        String filter = "_id = " + id;
        Cursor cursor = getContentResolver().query(STProvider.CONTENT_URI,
                DBOpenHelper.COLUMNS, filter, null, null);
        cursor.moveToFirst();

        obj.id = Integer.parseInt(id);
        obj.name = cursor.getString(cursor.getColumnIndex("name"));
        obj.parent = cursor.getInt(cursor.getColumnIndex("parent"));
        obj.data1 = cursor.getString(cursor.getColumnIndex("data1"));
        obj.data2 = cursor.getString(cursor.getColumnIndex("data2"));
        obj.data3 = cursor.getString(cursor.getColumnIndex("data3"));
        obj.data4 = cursor.getString(cursor.getColumnIndex("data4"));
        obj.type = TrackerType.getTypeFromString(cursor.getString(cursor.getColumnIndex("type")));
        return obj;
    }

    protected void updateData(TrackerObject obj) {
        // TODO: replace the hard-coded strings
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("type", obj.type.toString());
        values.put("parent", obj.parent);
        values.put("data1", obj.data1);
        values.put("data2", obj.data2);
        values.put("data3", obj.data4);
        values.put("data4", obj.data4);
        String filter = "_id = " + obj.id;

        getContentResolver().update(STProvider.CONTENT_URI, values, filter, null);
        // todo: param for toast messages
        Toast.makeText(this, getType() + " updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    protected String insertData(TrackerObject obj) {
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
        getContentResolver().delete(STProvider.CONTENT_URI, filter, filterValues);
        Toast.makeText(this, getType() + " deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    //endregion
}
