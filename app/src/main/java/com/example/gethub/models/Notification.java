// File: com.example.gethub.models.Notification.java
package com.example.gethub.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores notification messages for status updates and alerts.
 */
public class Notification implements Parcelable {

    private String notificationId;
    private String studentId; // Recipient ID (links to User)
    private String message; // The content (e.g., "Your request TR-001 has been Approved")
    private Long timestamp; // When the notification was created
    private boolean isRead; // Status for the user interface
    private String linkedId; // The ID of the linked entity (Ticket ID or Appointment ID)
    private String notificationType; // e.g., "STATUS_UPDATE", "APPOINTMENT_REMINDER"

    // --- Constructor ---
    public Notification(String notificationId, String studentId, String message, String linkedId, String notificationType) {
        this.notificationId = notificationId;
        this.studentId = studentId;
        this.message = message;
        this.linkedId = linkedId;
        this.notificationType = notificationType;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false; // Default to unread
    }

    // --- Getters ---
    public String getNotificationId() { return notificationId; }
    public String getStudentId() { return studentId; }
    public String getMessage() { return message; }
    public Long getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public String getLinkedId() { return linkedId; }
    public String getNotificationType() { return notificationType; }

    // --- Setters / Utility ---
    public void setRead(boolean read) { this.isRead = read; }


    // --- Parcelable Implementation ---

    protected Notification(Parcel in) {
        notificationId = in.readString();
        studentId = in.readString();
        message = in.readString();
        timestamp = in.readLong();
        isRead = in.readByte() != 0;
        linkedId = in.readString();
        notificationType = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(notificationId);
        dest.writeString(studentId);
        dest.writeString(message);
        dest.writeLong(timestamp);
        dest.writeByte((byte) (isRead ? 1 : 0));
        dest.writeString(linkedId);
        dest.writeString(notificationType);
    }
}