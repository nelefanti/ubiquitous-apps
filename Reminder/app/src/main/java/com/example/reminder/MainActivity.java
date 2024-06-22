package com.example.reminder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    private SwitchMaterial toggleButton;
    private ImageButton addReminderButton;
    private Button newReminderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new FeedReminderDbHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        toggleButton = findViewById(R.id.toggleButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(this, reminderList);
        recyclerView.setAdapter(reminderAdapter);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminderAdapter.setShowCompleted(!isChecked);
        });

        reminderText = findViewById(R.id.reminderText);
        reminderNote = findViewById(R.id.reminderNote);
        addReminderButton = findViewById(R.id.addReminderButton);

        addReminderButton.setOnClickListener(v -> {
            reminderText.setVisibility(View.VISIBLE);
            reminderNote.setVisibility(View.VISIBLE);
            newReminderButton.setVisibility(View.VISIBLE);
            addReminderButton.setVisibility(View.GONE);
        });

        newReminderButton = findViewById(R.id.newReminder);

        newReminderButton.setOnClickListener(v -> {
            String title = reminderText.getText().toString();
            String note = reminderNote.getText().toString();
            addReminder(title, note);
            reminderText.setText("");
            reminderNote.setText("");
            reminderText.setVisibility(View.GONE);
            reminderNote.setVisibility(View.GONE);
            newReminderButton.setVisibility(View.GONE);
            addReminderButton.setVisibility(View.VISIBLE);
            loadReminders();
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Reminder reminder = reminderList.get(position);
                reminderAdapter.deleteReminder(reminder.getId());
                reminderAdapter.removeReminderAt(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    Paint p = new Paint();
                    if (dX < 0) {
                        p.setColor(Color.RED);
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                        p.setColor(Color.WHITE);
                        p.setTextSize(getResources().getDimensionPixelSize(R.dimen.delete_text_size));
                        String text = "Delete";
                        float textWidth = p.measureText(text);
                        float textX = itemView.getRight() - textWidth - getResources().getDimensionPixelSize(R.dimen.delete_text_margin);
                        float textY = itemView.getTop() + ((itemView.getBottom() - itemView.getTop()) / 2) + (p.getTextSize() / 2);
                        c.drawText(text, textX, textY, p);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        loadReminders();
    }

    private void addReminder(String title, String note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_NOTE, note);
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_DONE, 0);
        db.insert(FeedReminder.FeedEntry.TABLE_NAME, null, values);
    }

    public void loadReminders() {
        reminderList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                FeedReminder.FeedEntry.TABLE_NAME,
                null,
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
            boolean done = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReminder.FeedEntry.COLUMN_NAME_DONE)) == 1;
            reminderList.add(new Reminder(id, title, note, done));
        }
        cursor.close();
        reminderAdapter.notifyDataSetChanged();
    }
}
