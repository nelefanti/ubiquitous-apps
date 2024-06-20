package com.example.reminder;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditDialogFragment extends DialogFragment {

    private EditText editTitle, editNote;
    private Button btnSave, btnCancel;
    private FeedReminderDbHelper dbHelper;
    private long reminderId;

    public EditDialogFragment(long id) {
        reminderId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_dialog, container, false);
        editTitle = view.findViewById(R.id.editTitle);
        editNote = view.findViewById(R.id.editNote);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        dbHelper = new FeedReminderDbHelper(getContext());

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String note = editNote.getText().toString();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            updateReminder(reminderId, title, note);
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        loadReminderData(reminderId);

        return view;
    }

    private void loadReminderData(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                FeedReminder.FeedEntry.COLUMN_NAME_TITLE,
                FeedReminder.FeedEntry.COLUMN_NAME_NOTE
        };
        String selection = FeedReminder.FeedEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                FeedReminder.FeedEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedReminder.FeedEntry.COLUMN_NAME_TITLE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(FeedReminder.FeedEntry.COLUMN_NAME_NOTE));

            editTitle.setText(title);
            editNote.setText(note);
        }
        cursor.close();
    }

    private void updateReminder(long id, String title, String note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedReminder.FeedEntry.COLUMN_NAME_NOTE, note);

        String selection = FeedReminder.FeedEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        db.update(FeedReminder.FeedEntry.TABLE_NAME, values, selection, selectionArgs);
        ((MainActivity) getActivity()).loadReminders();
    }
}
