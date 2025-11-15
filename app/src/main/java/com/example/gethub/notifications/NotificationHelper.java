// File: com.example.gethub.notifications.NotificationHelper.java (FINAL FIX)
package com.example.gethub.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.gethub.GethubApplication;
import com.example.gethub.R;
// We NO LONGER need DataRepository or Notification imports here
import com.example.gethub.models.RequestTicket;
import com.example.gethub.requests.TicketDetailActivity;

public class NotificationHelper {

    // This method NOW ONLY handles the on-screen push notification.
    // The ViewModel handles saving the record to the database.
    public static void showTicketNotification(Context context, RequestTicket ticket) {

        // --- 1. CRASH PREVENTION (Robustness Check) ---
        // This stops the helper from crashing if the ticket is null.
        if (ticket == null || ticket.getTicketId() == null) {
            return;
        }
        // --- END CHECK ---

        // 2. Create the Intent for when the user taps the notification
        Intent intent = new Intent(context, TicketDetailActivity.class);
        intent.putExtra(TicketDetailActivity.EXTRA_TICKET, ticket);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Use the ticketId's hashcode for a unique PendingIntent ID
        int uniqueId = ticket.getTicketId().hashCode();

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                uniqueId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 3. Build the notification content
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, GethubApplication.STATUS_CHANNEL_ID)
                // --- 2. CRASH FIX (Guaranteed Icon) ---
                // Use the app's main launcher icon. This is guaranteed to exist and
                // will 100% fix the silent Resource.NotFoundException crash.
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Request Submitted")
                .setContentText(String.format("Ticket %s: Your request is now '%s'.", ticket.getTicketId(), ticket.getStatus()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // 4. Issue the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(uniqueId, builder.build());
    }
}