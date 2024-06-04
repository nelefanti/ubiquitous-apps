package com.example.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminderList;
    private FeedReminderDbHelper dbHelper;
    private Handler handler;

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView titleTextView;
        public TextView noteTextView;
        public TextView doneTextView;
        public TextView dueDateTextView;
        public Button deleteButton;

        public ReminderViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            titleTextView = view.findViewById(R.id.titleTextView);
            noteTextView = view.findViewById(R.id.noteTextView);
            doneTextView = view.findViewById(R.id.doneTextView);
            dueDateTextView = view.findViewById(R.id.dueDateTextView);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    public ReminderAdapter(Context context, List<Reminder> reminderList) {
        this.reminderList = reminderList;
        this.dbHelper = new FeedReminderDbHelper(context);
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.titleTextView.setText(reminder.getTitle());
        holder.noteTextView.setText(reminder.getNote());
        holder.doneTextView.setText(reminder.isDone() ? "Done" : "Not Done");
        holder.dueDateTextView.setText(reminder.getDueDate());
        holder.checkBox.setChecked(reminder.isDone());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateReminderDoneStatus(reminder.getId(), isChecked);
            reminder.setDone(isChecked); // Update the reminder object
            // Post the notifyItemChanged call to the handler to ensure it executes after the current layout pass
            handler.post(() -> notifyItemChanged(position));
        });

        holder.deleteButton.setOnClickListener(v -> {
            deleteReminder(reminder.getId());
            removeReminderAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    private void updateReminderDoneStatus(long id, boolean done) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_DONE, done ? 1 : 0);
        String selection = FeedReminder.FeedEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.update(FeedReminder.FeedEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    private void deleteReminder(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = FeedReminder.FeedEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(FeedReminder.FeedEntry.TABLE_NAME, selection, selectionArgs);
    }

    private void removeReminderAt(int position) {
        reminderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, reminderList.size());
    }
}
