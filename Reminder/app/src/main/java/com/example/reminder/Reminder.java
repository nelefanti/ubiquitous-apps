package com.example.reminder;

public class Reminder {
    private long id;
    private String title;
    private String note;
    private boolean done;
    private String dueDate;

    public Reminder(long id, String title, String note, boolean done) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.done = done;
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
    }
}
