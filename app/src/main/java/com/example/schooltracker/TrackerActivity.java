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
import android.util.Log;
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
    protected String filterChildren;
    protected String[] filterValues = new String[]{"",""};
    protected String[] filterValuesChildren = new String[]{"",""};
    protected String action;
    protected TrackerObject existingData;
    protected String selectedFromList;

    protected EditText editorName;
    protected TextView editorData1;
    protected TextView editorData2;
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
            getLoaderManager().initLoader(0, null, this);

        } else {
            getLoaderManager().initLoader(1, null, this);
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

            /*
            Cursor cursor = getContentResolver().query(contentURI,
                    DBOpenHelper.COLUMNS, filter, filterValues, null, null);

            cursorAdapter = new SimpleCursorAdapter(this, layout, cursor, from, to, 0);
            */
            cursorAdapter = new SimpleCursorAdapter(this, layout, null, from, to, 0); //todo: changed cursor to null
        }
    }

    public void initControls() {
        if (isDetailActivity) {
            editorName = (EditText) findViewById(R.id.editName);
            editorData1 = (TextView) findViewById(R.id.tvData1);
            editorData2 = (TextView) findViewById(R.id.tvData2);
            editorChildList = (ListView) findViewById(R.id.listChildren);
            Button editorAddChildBtn = (Button) findViewById(R.id.btnAddChild);
            Button editorRemoveChildBtn = (Button) findViewById(R.id.btnRemoveChild);
            TextView editorChildrenLbl = (TextView) findViewById(R.id.lblChildren);

            String[] labels;

            switch (type) {
                case TERM:
                    labels = new String[]{"Term name",
                            "Start Date: ",
                            "End Date: ",
                            "Course"};
                    break;
                case COURSE:
                    labels = new String[]{"Course name",
                            "Start Date: ",
                            "End Date: ",
                            "Assessment"};
                    break;
                case ASSESSMENT:
                    labels = new String[]{"Assessment name",
                            "Due Date: ",
                            "",
                            "Note"};
                    break;
                case MENTOR:
                    labels = new String[]{"Mentor name",
                            "Email: ",
                            "Phone: ",
                            "Course"};
                    break;
                case NOTE:
                    labels = new String[]{"Note title",
                            "Add text",
                            "",
                            ""};
                    break;
                case ALERT:
                    labels = new String[]{"Alert title",
                            "Alert Date: ",
                            "",
                            "Assessment"};
                    break;
                default:
                    labels = new String[]{"Name",
                            "Data1: ",
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
            Uri uri = intent.getParcelableExtra(STProvider.CONTENT_ITEM_TYPE);

            if (uri == null) {
                action = intent.ACTION_INSERT;
                setTitle("New " + getType());
            } else {
                action = intent.ACTION_EDIT;
                setTitle("Edit " + getType());
                filter = "type = ? AND _id = ?";
                filterValues = new String[]{type.toString(), uri.getLastPathSegment()};


                Cursor cursor = getContentResolver().query(uri,
                        DBOpenHelper.COLUMNS, filter, filterValues, null);
                cursor.moveToFirst();

                existingData.name = cursor.getString(cursor.getColumnIndex("name"));
                existingData.parent = cursor.getInt(cursor.getColumnIndex("parent"));
                existingData.data1 = cursor.getString(cursor.getColumnIndex("data1"));
                existingData.data2 = cursor.getString(cursor.getColumnIndex("data2"));

                cursorAdapterChildren = new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_1,
                        null,
                        new String[]{"name"},
                        new int[]{android.R.id.text1}, 0);

                editorChildList.setAdapter(cursorAdapterChildren);

                editorChildList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            selectedFromList = (String) (editorChildList.getItemAtPosition(position));
                    }
                });

                editorName.setText(existingData.name);
                editorName.requestFocus();
                editorData1.setText(existingData.data1);
                editorData2.setText(existingData.data2);
            }
        } else {  // summary activity
            ListView list = (ListView) findViewById(android.R.id.list);
            list.setAdapter(cursorAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(TrackerActivity.this, detailClass);
                    Uri uri = Uri.parse(STProvider.CONTENT_URI + "/" + id);
                    intent.putExtra(STProvider.CONTENT_ITEM_TYPE, uri);
                    startActivityForResult(intent, TrackerObject.REQUEST_CODE);
                }
            });
        }
    }

    //endregion

    private String getType() {
        String s = type.toString();
        s = s.substring(0,1) + s.substring(1).toLowerCase();
        return s;
    }

    public void createNewObject(View view) {
        Intent intent = new Intent(this, detailClass);
        startActivityForResult(intent, TrackerObject.REQUEST_CODE);
    }

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
            case MENTOR:
                desc = "Email: ";
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
            case MENTOR:
                desc = "Phone: ";
                break;
            default:
                desc = "";
        }
        tv.setText(desc + data);
    }

    public void addChild(View view) {
        Intent intent = new Intent(this, childClass);
        startActivityForResult(intent, TrackerObject.REQUEST_CODE);
    }

    public void removeChild(View view) {
        if (selectedFromList != null)
            Log.d("Message",selectedFromList);
        else
            Log.d("Message","Nothing selected");
        /*getContentResolver().delete(STProvider.CONTENT_URI, filter, filterValues);
        Toast.makeText(this, getType() + " deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!isDetailActivity){
            if(requestCode == TrackerObject.REQUEST_CODE && resultCode == RESULT_OK) {
                restartLoader(0);
            }
        } else {
            if(requestCode == TrackerObject.REQUEST_CODE && resultCode == RESULT_OK) {
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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isDetailActivity) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finishEditing();
                    break;
                case R.id.action_delete:
                    deleteData();
                    break;
            }
        }
        return true;
    }

    private void restartLoader(int loader) {
        getLoaderManager().restartLoader(loader, null, this);
    }

    protected void finishEditing() {
        // TODO: add functionality here for children list
        TrackerObject newObj = new TrackerObject();
        newObj.name = editorName.getText().toString().trim();
        newObj.type = this.type;
        newObj.data1 = editorData1.getText().toString();
        newObj.data2 = editorData2.getText().toString();

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
        finish();
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

    //endregion

    //region Data Handling

    protected void updateData(TrackerObject obj) {
        // TODO: replace the hard-coded strings
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("type", obj.type.toString());
        values.put("parent", obj.parent);
        values.put("data1", obj.data1);
        values.put("data2", obj.data2);
        getContentResolver().update(STProvider.CONTENT_URI, values, filter, filterValues);
        Toast.makeText(this, getType() + " updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    protected void insertData(TrackerObject obj) {
        // TODO: replace the hard-coded strings
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("type", obj.type.toString());
        values.put("parent", obj.parent);
        values.put("data1", obj.data1);
        values.put("data2", obj.data2);
        getContentResolver().insert(STProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    private void deleteData() {
        getContentResolver().delete(STProvider.CONTENT_URI, filter, filterValues);
        Toast.makeText(this, getType() + " deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    //endregion
}
