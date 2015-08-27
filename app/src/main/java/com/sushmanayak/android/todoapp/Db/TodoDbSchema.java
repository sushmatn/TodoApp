package com.sushmanayak.android.todoapp.Db;

/**
 * Created by SushmaNayak on 8/25/2015.
 */
public class TodoDbSchema {
    public static final class TodoTable {
        public static final String NAME = "todos";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DESCRIPTION = "desc";
            public static final String DATE = "date";
            public static final String PRIORITY = "priority";
            public static final String NOTIFY = "notify";
            public static final String COMPLETED = "completed";
        }
    }
}
