package com.example.lucas.haushaltsmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.lucas.haushaltsmanager.Services.BackupCreatorService;


/**
 * Mit dieser Klasse kann ich den BackupService periodisch aufrufen lassen
 *
 * Anleiung siehe: https://guides.codepath.com/android/Starting-Background-Services#using-with-alarmmanager-for-periodic-tasks
 */
public class MyAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, BackupCreatorService.class);
        context.startService(i);
    }
}
