// File: com.example.gethub.models.SystemDocument.java
package com.example.gethub.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a static document available for request, along with its rules and fees.
 * Used to populate the Request Document page dropdown and for submission validation.
 */
public class SystemDocument implements Parcelable {

    private String docCode; // e.g., "TOR", "GM"
    private String docName; // e.g., "Transcript of Records", "Good Moral Certificate"
    private String processingTime; // e.g., "5 business days", "Instant"
    private double serviceFee;
    private List<String> requirements;
    private String deliveryConstraint; // "Pick-up Only", "Digital Only", "Both"
    private boolean isInstant; // Determines immediate completion/approval

    // --- Constructor ---
    public SystemDocument(String docCode, String docName, String processingTime, double serviceFee, String deliveryConstraint, boolean isInstant) {
        this.docCode = docCode;
        this.docName = docName;
        this.processingTime = processingTime;
        this.serviceFee = serviceFee;
        this.deliveryConstraint = deliveryConstraint;
        this.isInstant = isInstant;
        this.requirements = new ArrayList<>(); // Initialize empty list
    }

    // --- Getters ---
    public String getDocCode() { return docCode; }
    public String getDocName() { return docName; }
    public String getProcessingTime() { return processingTime; }
    public double getServiceFee() { return serviceFee; }
    public List<String> getRequirements() { return requirements; }
    public String getDeliveryConstraint() { return deliveryConstraint; }
    public boolean isInstant() { return isInstant; }

    // --- Setters / Utility ---
    public void setRequirements(List<String> requirements) { this.requirements = requirements; }


    // --- Parcelable Implementation ---

    protected SystemDocument(Parcel in) {
        docCode = in.readString();
        docName = in.readString();
        processingTime = in.readString();
        serviceFee = in.readDouble();
        deliveryConstraint = in.readString();
        isInstant = in.readByte() != 0;
        requirements = in.createStringArrayList();
    }

    public static final Creator<SystemDocument> CREATOR = new Creator<SystemDocument>() {
        @Override
        public SystemDocument createFromParcel(Parcel in) {
            return new SystemDocument(in);
        }

        @Override
        public SystemDocument[] newArray(int size) {
            return new SystemDocument[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(docCode);
        dest.writeString(docName);
        dest.writeString(processingTime);
        dest.writeDouble(serviceFee);
        dest.writeString(deliveryConstraint);
        dest.writeByte((byte) (isInstant ? 1 : 0));
        dest.writeStringList(requirements);
    }
}