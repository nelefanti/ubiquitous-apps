package com.example.reminder;

import android.provider.BaseColumns;

public final class FeedReminder {
    private FeedReminder() {}

    /* Innere Klasse, die den Tabelleninhalt definiert */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_DONE = "done";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";
    }
}
