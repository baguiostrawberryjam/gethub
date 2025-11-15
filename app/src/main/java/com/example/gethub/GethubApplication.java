package com.example.gethub;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class GethubApplication extends Application {

    public static final String STATUS_CHANNEL_ID = "status_updates";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        // The NotificationChannel class is new and not in the support library, so it only runs on Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel for Status Updates
            NotificationChannel statusChannel = new NotificationChannel(
                    STATUS_CHANNEL_ID,
                    "Request Status Updates",
                    NotificationManager.IMPORTANCE_HIGH
            );
            statusChannel.setDescription("Notifications for when the status of your document request changes.");

            // Register the channel with the system
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(statusChannel);
            }
        }
    }
}
