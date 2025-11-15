// File: com.example.gethub.notifications.NotificationHelper.java
package com.example.gethub.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.gethub.GethubApplication;
import com.example.gethub.R;
import com.example.gethub.models.RequestTicket;
import com.example.gethub.requests.TicketDetailActivity;

public class NotificationHelper {

    // Static method to build and show the notification
    public static void showTicketNotification(Context context, RequestTicket ticket) {

        // 1. Create an Intent that opens the TicketDetailActivity when clicked
        Intent intent = new Intent(context, TicketDetailActivity.class);
        intent.putExtra(TicketDetailActivity.EXTRA_TICKET, ticket);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Ensure FLAG_IMMUTABLE is used as required for modern Android
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                ticket.getTicketId().hashCode(), // Unique request code based on ticket ID
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 2. Build the notification content
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, GethubApplication.STATUS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_filled) // Use an existing icon or create a new one
                .setContentTitle("Request Status Update")
                .setContentText(String.format("Ticket %s: Status changed to %s.", ticket.getTicketId(), ticket.getStatus()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // 3. Issue the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(ticket.getTicketId().hashCode(), builder.build());
    }
}