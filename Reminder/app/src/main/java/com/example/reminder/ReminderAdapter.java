package com.example.reminder;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminderList;
    private FeedReminderDbHelper dbHelper;
    private FragmentActivity activity;
    private boolean showCompleted = true;

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public ImageButton editBtn;
        public TextView titleTextView;
        public TextView noteTextView;

        public ReminderViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            titleTextView = view.findViewById(R.id.titleTextView);
            noteTextView = view.findViewById(R.id.noteTextView);
            editBtn = view.findViewById(R.id.editBtn);
        }
    }

    public ReminderAdapter(FragmentActivity activity, List<Reminder> reminderList) {
        this.reminderList = reminderList;
        this.dbHelper = new FeedReminderDbHelper(activity);
        this.activity = activity;
    }


    public void setShowCompleted(boolean showCompleted) {
        this.showCompleted = showCompleted;
        notifyDataSetChanged();
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

        if (!showCompleted && reminder.isDone()) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.titleTextView.setText(reminder.getTitle());
            holder.noteTextView.setText(reminder.getNote());
            holder.checkBox.setChecked(reminder.isDone());


            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateReminderDoneStatus(reminder.getId(), isChecked);
                reminder.setDone(isChecked);
            });


            holder.editBtn.setOnClickListener(v -> {
                EditDialogFragment editDialogFragment = new EditDialogFragment(reminder.getId());
                editDialogFragment.show(activity.getSupportFragmentManager(), "editDialog");
            });
        }
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }


    public void updateReminderDoneStatus(long id, boolean done) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_DONE, done ? 1 : 0);
        String selection = FeedReminder.FeedEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.update(FeedReminder.FeedEntry.TABLE_NAME, values, selection, selectionArgs);
    }


    public void deleteReminder(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = FeedReminder.FeedEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(FeedReminder.FeedEntry.TABLE_NAME, selection, selectionArgs);
    }


    public void removeReminderAt(int position) {
        reminderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, reminderList.size());
    }
}
