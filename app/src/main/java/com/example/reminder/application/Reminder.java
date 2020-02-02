package com.example.reminder.application;

import android.icu.util.Calendar;

import java.io.Serializable;

public class Reminder implements Serializable {
    private long id;
    private String title;
    private String content;
    private Calendar date;

    public Reminder(String title, String content, Calendar date) {
        this.title = title;
        this.content = content;
        this.date = date;
        id = date.getTimeInMillis();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getDateAsString() {
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);

        return dayOfMonth + "/" + month + "/" + year;
    }

    public String getTimeAsString() {
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        if ((hour == 0) || (minute == 0)) {
            return hour + "0" + ":" + minute + "0";
        } else {
            return hour + ":" + minute;
        }
    }

    public long getId() {
        return id;
    }
}
