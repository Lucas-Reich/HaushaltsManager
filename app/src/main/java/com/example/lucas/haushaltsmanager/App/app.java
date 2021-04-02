package com.example.lucas.haushaltsmanager.App;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.R;

import java.util.UUID;

public class app extends Application {
    public static final UUID transferCategoryId = UUID.fromString("eed05d4b-866d-4f20-b580-c50901066d73");
    public static final UUID unassignedCategoryId = UUID.fromString("a56664b4-e1c6-44aa-90ec-399cfe925cb4");

    private static final String CHANNEL_ID = "reminder";
    private static Context context;

    public static Context getContext() {
        return context;
    }

    /**
     * Es ist kein Problem NotificationChannels mehrfach zu erstellen.
     * Sollte ich einen Channel erneut erstellen, passiert einfach nichts.
     * Quelle: https://developer.android.com/training/notify-user/build-notification#Priority
     *
     * @return The channel id
     */
    public static String createReminderNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            String description = context.getString(R.string.channel_description);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.enableLights(true);

            context.getSystemService(NotificationManager.class)
                    .createNotificationChannel(channel);
        }

        return CHANNEL_ID;
    }

    public static UUID getNilUuid() {
        return new UUID(0, 0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();

        initializeDatabase();

    }

    private void initializeDatabase() {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));
    }
}
