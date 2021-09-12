package com.example.lucas.haushaltsmanager.entities;

import androidx.annotation.DrawableRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;

import java.util.concurrent.TimeUnit;

public class NotificationVO {
    private String title;
    private String content;
    @DrawableRes
    private int icon;

    public NotificationVO(String title, String content, @DrawableRes int icon) {
        this.title = title;
        this.content = content;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @DrawableRes
    public int getIcon() {
        return icon;
    }

    public Delay getDelay() {
        return new Delay(
                TimeUnit.MILLISECONDS,
                timeUntilExecution()
        );
    }

    private long timeUntilExecution() {
        long currentReminderTime = getReminderTime().toMillis();
        long now = System.currentTimeMillis();

        if (notificationOccursToday(currentReminderTime)) {
            return currentReminderTime - now;
        }

        long nextReminderTime = getNextReminderTime(currentReminderTime);
        return nextReminderTime - now;
    }

    private long getNextReminderTime(long reminderTime) {
        return reminderTime + TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS);
    }

    private boolean notificationOccursToday(long reminderTime) {
        return System.currentTimeMillis() < reminderTime;
    }

    private Time getReminderTime() {
        return new UserSettingsPreferences(app.getContext())
                .getReminderTime();
    }
}
