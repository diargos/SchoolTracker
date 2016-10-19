package com.example.schooltracker.Model;

import com.example.schooltracker.TrackerType;

public class TrackerObject {
    public static final int NORMAL_REQUEST = 1001;
    public static final int ADD_CHILD_REQUEST = 1002;

    public int id;
    public String name;
    public TrackerType type;
    public int parent;
    public String data1;
    public String data2;
    public String data3;
    public String data4;
}
