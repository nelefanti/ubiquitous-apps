package com.example.reminder;

import android.content.Context;
import android.os.Vibrator;
public class Reminder {
    private long id;
    private String title;
    private String note;
    private boolean done;
    private String dueDate;
    private Context context;

    public Reminder(long id, String title, String note, boolean done, Context context) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.done = done;
        this.context = context;
        this.dueDate = dueDate;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public boolean isDone() {
        return done;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDone(boolean done) {
        this.done = done;
        triggerHapticFeedback();
    }

    public void triggerHapticFeedback() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(1);
        }
    }
}
