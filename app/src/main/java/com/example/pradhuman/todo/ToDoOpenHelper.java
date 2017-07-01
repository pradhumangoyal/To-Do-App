package com.example.pradhuman.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pradhuman on 30-06-2017.
 */

public class ToDoOpenHelper extends SQLiteOpenHelper {
    public final static String TO_DO_ID ="_id";
    public final static String TO_DO_TABLE_NAME = "ToDO";
    public final static String TO_DO_TABLE_NAME_TWO = "ToDoTWO";
    public final static String TO_DO_TITLE = "title";
    public final static String TO_DO_TIME = "time";
    public final static String TO_DO_DATE = "date";
    public final static String TO_DO_CATEGORY = "category";
    public final static String TO_DO_DESC = "description";
    public final static String TO_DO_PRIORITY = "priority";
    public final static String TO_DO_IS_CHECKED = "isChecked";

    public ToDoOpenHelper(Context context) {
        super(context, "ToDo.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table " + TO_DO_TABLE_NAME + "( " + TO_DO_ID + " integer primary key autoincrement, "
                + TO_DO_TITLE + " text, " + TO_DO_CATEGORY + " text, " + TO_DO_TIME + " text, " + TO_DO_DATE + " text, "
                + TO_DO_DESC + " text, "+
                TO_DO_PRIORITY + " integer, " + TO_DO_IS_CHECKED + " integer);";
        String query2 = "create table " + TO_DO_TABLE_NAME_TWO + "( " + TO_DO_ID + " integer, "
                + TO_DO_TITLE + " text, " + TO_DO_CATEGORY + " text, " + TO_DO_TIME + " text, " + TO_DO_DATE + " text, "
                + TO_DO_DESC + " text, "+
                TO_DO_PRIORITY + " integer, " + TO_DO_IS_CHECKED + " integer);";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}