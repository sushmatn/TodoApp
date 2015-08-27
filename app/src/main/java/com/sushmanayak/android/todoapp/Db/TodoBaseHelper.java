package com.sushmanayak.android.todoapp.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SushmaNayak on 8/25/2015.
 */
public class TodoBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "todoBase.db";

    public TodoBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + TodoDbSchema.TodoTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                        TodoDbSchema.TodoTable.Cols.UUID + ", " +
                        TodoDbSchema.TodoTable.Cols.TITLE + ", " +
                        TodoDbSchema.TodoTable.Cols.DESCRIPTION + ", " +
                        TodoDbSchema.TodoTable.Cols.DATE + ", " +
                        TodoDbSchema.TodoTable.Cols.PRIORITY + ", " +
                        TodoDbSchema.TodoTable.Cols.NOTIFY + ", " +
                        TodoDbSchema.TodoTable.Cols.COMPLETED +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
