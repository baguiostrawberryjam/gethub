// File: com.example.gethub.notifications.NotificationAdapter.java
package com.example.gethub.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gethub.R;
import com.example.gethub.models.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_notification.xml layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    // Method to update the list when LiveData changes
    public void updateList(List<Notification> newList) {
        this.notificationList = newList;
        notifyDataSetChanged();
    }


    // --- View Holder (Handles the UI elements of one item) ---
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView tvNotificationMessage;
        TextView tvNotificationTime;
        TextView tvNotificationStatus; // Used to display 'View Ticket' or 'Marked Read'

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            // Adhering to your standard naming practices for TextViews (tvName)
            tvNotificationMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvNotificationTime = itemView.findViewById(R.id.tvNotificationTime);
            tvNotificationStatus = itemView.findViewById(R.id.tvNotificationStatus);

            // FUTURE: Add click listener to mark as read or navigate to ticket
        }

        public void bind(Notification notification) {
            tvNotificationMessage.setText(notification.getMessage());

            // Format timestamp to relative time (e.g., "5 minutes ago")
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
            tvNotificationTime.setText(sdf.format(new Date(notification.getTimestamp())));

            // Display status based on isRead state
            if (notification.isRead()) {
                tvNotificationStatus.setText("Read");
                tvNotificationStatus.setTextColor(itemView.getContext().getColor(R.color.gray));
            } else {
                tvNotificationStatus.setText("New/View Ticket");
            }
        }
    }
}