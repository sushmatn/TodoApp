package com.sushmanayak.android.todoapp.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sushmanayak.android.todoapp.model.TodoItem;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by SushmaNayak on 8/25/2015.
 */
public class TodoList {
    private static TodoList mTodoList;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static TodoList get(Context context) {
        if (mTodoList == null)
            mTodoList = new TodoList(context);
        return mTodoList;
    }

    private TodoList(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TodoBaseHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(TodoItem item) {
        ContentValues values = new ContentValues();
        values.put(TodoDbSchema.TodoTable.Cols.UUID, item.getId().toString());
        values.put(TodoDbSchema.TodoTable.Cols.TITLE, item.getTitle());
        values.put(TodoDbSchema.TodoTable.Cols.DESCRIPTION, item.getDescription());
        if(item.getDate()!= null)
            values.put(TodoDbSchema.TodoTable.Cols.DATE, item.getDate().getTime());
        else
            values.put(TodoDbSchema.TodoTable.Cols.DATE, 0);
        values.put(TodoDbSchema.TodoTable.Cols.PRIORITY, item.getPriority());
        values.put(TodoDbSchema.TodoTable.Cols.NOTIFY, item.getNotify());
        values.put(TodoDbSchema.TodoTable.Cols.COMPLETED, item.isCompleted() ? 1 : 0);

        return values;
    }

    public ArrayList<TodoItem> getTodoItems() {
        ArrayList<TodoItem> todoItems = new ArrayList<>();
        TodoCursorWrapper cursor = queryTodoItems(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                todoItems.add(cursor.getItem());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return todoItems;
    }

    public TodoItem getItem(UUID id) {
        TodoCursorWrapper cursor = queryTodoItems(
                TodoDbSchema.TodoTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            return cursor.getItem();
        } finally {
            cursor.close();
        }
    }

    public void addItem(TodoItem item) {
        ContentValues values = getContentValues(item);
        mDatabase.insert(TodoDbSchema.TodoTable.NAME, null, values);
    }

    public void updateItem(TodoItem item) {
        String uuidString = item.getId().toString();
        ContentValues values = getContentValues(item);

        mDatabase.update(TodoDbSchema.TodoTable.NAME, values, TodoDbSchema.TodoTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public void removeItem(TodoItem item){
        String uuidString = item.getId().toString();
        ContentValues values = getContentValues(item);

        mDatabase.delete(TodoDbSchema.TodoTable.NAME, TodoDbSchema.TodoTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    private TodoCursorWrapper queryTodoItems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TodoDbSchema.TodoTable.NAME,
                null,   // Columns - null selects all columns
                whereClause,
                whereArgs,
                null,   // groupBy
                null,   // having
                null    // orderBy
        );
        return new TodoCursorWrapper(cursor);
    }
}
