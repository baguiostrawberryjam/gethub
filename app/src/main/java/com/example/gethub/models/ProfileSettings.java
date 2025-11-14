// File: com.example.gethub.models.ProfileSettings.java
package com.example.gethub.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores user preferences, security settings, and editable profile attributes.
 */
public class ProfileSettings implements Parcelable {

    private String studentId; // Links to the User object

    // Profile Fields
    private String profilePictureUrl; // Path to the editable profile picture image file.

    // Settings -> Preferences
    private String themePreference; // "Light", "Dark", or "System Default"
    private boolean notificationEnabled; // Global toggle for notifications

    // Settings -> Account Security
    private long sessionTimeoutDuration; // Duration in milliseconds before auto-logout (e.g., 30 mins)
    private boolean twoFactorEnabled; // Optional 2FA setting

    // --- Constructor ---
    public ProfileSettings(String studentId) {
        this.studentId = studentId;
        // Default Settings
        this.profilePictureUrl = "";
        this.themePreference = "System Default";
        this.notificationEnabled = true;
        this.sessionTimeoutDuration = 1800000; // Default to 30 minutes
        this.twoFactorEnabled = false;
    }

    // --- Getters ---
    public String getStudentId() { return studentId; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public String getThemePreference() { return themePreference; }
    public boolean isNotificationEnabled() { return notificationEnabled; }
    public long getSessionTimeoutDuration() { return sessionTimeoutDuration; }
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }

    // --- Setters ---
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public void setThemePreference(String themePreference) { this.themePreference = themePreference; }
    public void setNotificationEnabled(boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
    public void setSessionTimeoutDuration(long sessionTimeoutDuration) { this.sessionTimeoutDuration = sessionTimeoutDuration; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }


    // --- Parcelable Implementation ---

    protected ProfileSettings(Parcel in) {
        studentId = in.readString();
        profilePictureUrl = in.readString();
        themePreference = in.readString();
        notificationEnabled = in.readByte() != 0;
        sessionTimeoutDuration = in.readLong();
        twoFactorEnabled = in.readByte() != 0;
    }

    public static final Creator<ProfileSettings> CREATOR = new Creator<ProfileSettings>() {
        @Override
        public ProfileSettings createFromParcel(Parcel in) {
            return new ProfileSettings(in);
        }

        @Override
        public ProfileSettings[] newArray(int size) {
            return new ProfileSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(studentId);
        dest.writeString(profilePictureUrl);
        dest.writeString(themePreference);
        dest.writeByte((byte) (notificationEnabled ? 1 : 0));
        dest.writeLong(sessionTimeoutDuration);
        dest.writeByte((byte) (twoFactorEnabled ? 1 : 0));
    }
}