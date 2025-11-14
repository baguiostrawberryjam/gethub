// File: com.example.gethub.models.RequestTicket.java (FIXED DATA INTEGRITY)
package com.example.gethub.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestTicket implements Parcelable {

    private String ticketId;
    private String studentId;
    private String documentType; // FIELD IN QUESTION
    private String purposeOfRequest;
    private String deliveryMethod;
    private String status;
    private Long requestDate;
    private Long completionDate;
    private String additionalInstructions;
    private double serviceFee;
    private boolean isInstant;
    private String fileReferenceId;
    private String appointmentId;

    // --- Constructor ---
    public RequestTicket(String ticketId, String studentId, String documentType, String deliveryMethod, double serviceFee, boolean isInstant) {
        this.ticketId = ticketId;
        this.studentId = studentId;
        this.documentType = documentType;
        this.deliveryMethod = deliveryMethod;
        this.serviceFee = serviceFee;
        this.isInstant = isInstant;
        this.status = "Processing";
        this.requestDate = System.currentTimeMillis();
        this.completionDate = null; // Stays null (written as -1 in Parcelable)

        // FIX 1: Initialize all remaining String fields to prevent null corruption
        this.purposeOfRequest = ""; // Set to empty string
        this.additionalInstructions = ""; // Set to empty string
        this.fileReferenceId = null; // Set to null (Handled by Parcelable as string)
        this.appointmentId = null; // Set to null (Handled by Parcelable as string)
    }

    // --- Getters ---
    public String getTicketId() { return ticketId; }
    public String getStudentId() { return studentId; }
    public String getDocumentType() { return documentType; } // Used in TicketDetailActivity
    public String getPurposeOfRequest() { return purposeOfRequest; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public String getStatus() { return status; }
    public Long getRequestDate() { return requestDate; }
    public Long getCompletionDate() { return completionDate; }
    public String getAdditionalInstructions() { return additionalInstructions; }
    public double getServiceFee() { return serviceFee; }
    public boolean isInstant() { return isInstant; }
    public String getFileReferenceId() { return fileReferenceId; }
    public String getAppointmentId() { return appointmentId; }

    // --- Setters ---
    public void setPurposeOfRequest(String purposeOfRequest) { this.purposeOfRequest = purposeOfRequest; }
    public void setStatus(String status) { this.status = status; }
    public void setCompletionDate(Long completionDate) { this.completionDate = completionDate; }
    public void setAdditionalInstructions(String additionalInstructions) { this.additionalInstructions = additionalInstructions; }
    public void setFileReferenceId(String fileReferenceId) { this.fileReferenceId = fileReferenceId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setDocumentType(String documentType) { this.documentType = documentType; } // FIX: Essential setter
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public void setServiceFee(double serviceFee) { this.serviceFee = serviceFee; }
    public void setInstant(boolean instant) { isInstant = instant; }


    // --- Parcelable Implementation ---

    protected RequestTicket(Parcel in) {
        ticketId = in.readString();
        studentId = in.readString();
        documentType = in.readString();     // Document Type
        purposeOfRequest = in.readString();
        deliveryMethod = in.readString();
        status = in.readString();
        requestDate = in.readLong();
        completionDate = in.readLong() == -1 ? null : in.readLong();
        additionalInstructions = in.readString();
        serviceFee = in.readDouble();
        isInstant = in.readByte() != 0;
        fileReferenceId = in.readString();
        appointmentId = in.readString();
    }

    public static final Creator<RequestTicket> CREATOR = new Creator<RequestTicket>() {
        @Override
        public RequestTicket createFromParcel(Parcel in) {
            return new RequestTicket(in);
        }

        @Override
        public RequestTicket[] newArray(int size) {
            return new RequestTicket[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ticketId);
        dest.writeString(studentId);
        dest.writeString(documentType); // FIX: Write the field
        dest.writeString(purposeOfRequest);
        dest.writeString(deliveryMethod);
        dest.writeString(status);
        dest.writeLong(requestDate);
        dest.writeLong(completionDate == null ? -1 : completionDate);
        dest.writeString(additionalInstructions);
        dest.writeDouble(serviceFee);
        dest.writeByte((byte) (isInstant ? 1 : 0));
        dest.writeString(fileReferenceId);
        dest.writeString(appointmentId);
    }
}
