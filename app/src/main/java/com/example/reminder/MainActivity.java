package com.example.reminder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FeedReminderDbHelper dbHelper;
    private RecyclerView recyclerView;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminderList;
    private EditText reminderText;
    private EditText reminderNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new FeedReminderDbHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(this, reminderList);
        recyclerView.setAdapter(reminderAdapter);

        reminderText = findViewById(R.id.reminderText);
        reminderNote = findViewById(R.id.reminderNote);

        loadReminders();

        Button newReminderButton = findViewById(R.id.newReminder);
        newReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = reminderText.getText().toString();
                String note = reminderNote.getText().toString();
                addNewReminder(title, note, false, "2024-12-31");
                loadReminders();
                reminderText.setText(""); // Clear the text field after adding the reminder
                reminderNote.setText("");
            }
        });
    }

    private void addNewReminder(String title, String note, boolean done, String dueDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_NOTE, note);
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_DONE, done ? 1 : 0);
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_DUE_DATE, dueDate);
        db.insert(FeedReminder.FeedEntry.TABLE_NAME, null, values);
    }

    private void loadReminders() {
        reminderList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                FeedReminder.FeedEntry._ID,
                FeedReminder.FeedEntry.COLUMN_NAME_TITLE,
                FeedReminder.FeedEntry.COLUMN_NAME_NOTE,
                FeedReminder.FeedEntry.COLUMN_NAME_DONE,
                FeedReminder.FeedEntry.COLUMN_NAME_DUE_DATE
        };
        Cursor cursor = db.query(
                FeedReminder.FeedEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(FeedReminder.FeedEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedReminder.FeedEntry.COLUMN_NAME_TITLE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(FeedReminder.FeedEntry.COLUMN_NAME_NOTE));
            boolean done = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReminder.FeedEntry.COLUMN_NAME_DONE)) > 0;
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(FeedReminder.FeedEntry.COLUMN_NAME_DUE_DATE));
            reminderList.add(new Reminder(id, title, note, done, dueDate));
        }
        cursor.close();
        reminderAdapter.notifyDataSetChanged();
    }
}
