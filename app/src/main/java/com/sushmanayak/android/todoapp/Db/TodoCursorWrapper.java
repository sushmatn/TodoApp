package com.sushmanayak.android.todoapp.Db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sushmanayak.android.todoapp.model.TodoItem;

import java.util.Date;
import java.util.UUID;

/**
 * Created by SushmaNayak on 8/25/2015.
 */
public class TodoCursorWrapper extends CursorWrapper {
    public TodoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TodoItem getItem() {
        String uuidString = getString(getColumnIndex(TodoDbSchema.TodoTable.Cols.UUID));
        String title = getString(getColumnIndex(TodoDbSchema.TodoTable.Cols.TITLE));
        String desc = getString(getColumnIndex(TodoDbSchema.TodoTable.Cols.DESCRIPTION));
        long date = getLong(getColumnIndex(TodoDbSchema.TodoTable.Cols.DATE));
        int priority = getInt(getColumnIndex(TodoDbSchema.TodoTable.Cols.PRIORITY));
        boolean notify = getInt(getColumnIndex(TodoDbSchema.TodoTable.Cols.NOTIFY)) == 1 ? true : false;
        boolean isCompleted = getInt(getColumnIndex(TodoDbSchema.TodoTable.Cols.COMPLETED)) == 1 ? true : false;

        if (date == 0)
            return new TodoItem(UUID.fromString(uuidString), title, desc, null, priority, notify, isCompleted);
        else
            return new TodoItem(UUID.fromString(uuidString), title, desc, new Date(date), priority, notify, isCompleted);
    }
}
