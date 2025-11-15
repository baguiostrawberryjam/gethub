// File: com.example.gethub.models.Appointment.java
package com.example.gethub.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Tracks a scheduled appointment for document pick-up or other service.
 */
public class Appointment implements Parcelable {

    private String appointmentId;
    private String studentId; // Links to User
    private String ticketId; // Links to RequestTicket (if applicable)
    private Long scheduledDate; // Timestamp for chosen date and time
    private String status; // "Pending", "Approved", "Completed", "Canceled"
    private int rescheduleCount; // Max 1 based on rules
    private String qrCodeData; // Data for QR code generation

    // --- Constructor ---
    public Appointment(String appointmentId, String studentId, String ticketId, Long scheduledDate) {
        this.appointmentId = appointmentId;
        this.studentId = studentId;
        this.ticketId = ticketId;
        this.scheduledDate = scheduledDate;
        this.status = "Pending"; // Default initial status
        this.rescheduleCount = 0;
    }

    // --- Getters ---
    public String getAppointmentId() { return appointmentId; }
    public String getStudentId() { return studentId; }
    public String getTicketId() { return ticketId; }
    public Long getScheduledDate() { return scheduledDate; }
    public String getStatus() { return status; }
    public int getRescheduleCount() { return rescheduleCount; }
    public String getQrCodeData() { return qrCodeData; }

    // --- Setters / Utility (UPDATED) ---
    public void setScheduledDate(Long scheduledDate) { this.scheduledDate = scheduledDate; }
    public void setStatus(String status) { this.status = status; }
    public void setRescheduleCount(int rescheduleCount) { this.rescheduleCount = rescheduleCount; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    /** NEW: Setter required to link the Appointment back to its related Request Ticket. */
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    // --- Parcelable Implementation ---

    protected Appointment(Parcel in) {
        appointmentId = in.readString();
        studentId = in.readString();
        ticketId = in.readString();
        scheduledDate = in.readLong();
        status = in.readString();
        rescheduleCount = in.readInt();
        qrCodeData = in.readString();
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appointmentId);
        dest.writeString(studentId);
        dest.writeString(ticketId);
        dest.writeLong(scheduledDate);
        dest.writeString(status);
        dest.writeInt(rescheduleCount);
        dest.writeString(qrCodeData);
    }
}
