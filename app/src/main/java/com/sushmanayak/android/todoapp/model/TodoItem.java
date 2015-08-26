package com.sushmanayak.android.todoapp.model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by SushmaNayak on 8/25/2015.
 */
public class TodoItem {
    private UUID mId;
    private String mTitle;
    private String mDescription;
    private Date mDate;
    private int mPriority;
    private boolean mCompleted;

    public TodoItem() {
        mId = UUID.randomUUID();
        mPriority = 0;
        mCompleted = false;
    }

    public TodoItem(UUID Id, String title, String description, Date date, int priority, boolean completed) {
        mId = Id;
        mTitle = title;
        mDescription = description;
        mDate = date;
        mPriority = priority;
        mCompleted = completed;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void setCompleted(boolean mCompleted) {
        this.mCompleted = mCompleted;
    }

    public UUID getId() {
        return mId;
    }

    @Override
    public String toString() {
        if (mDate == null)
            return "\nTask: " + mTitle + "\n" + "Details: " + mDescription;
        else
            return "\nTask: " + mTitle + "\n" + "Details: " + mDescription + "\n" + "Due by: " + String.format("%1$tY %1$tb %1$td", mDate);
    }
}