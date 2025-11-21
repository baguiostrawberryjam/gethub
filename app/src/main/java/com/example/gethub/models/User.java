package com.example.gethub.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable model to hold all registration data across the 5 pages.
 */
public class User implements Parcelable {
    // Page 1: Names
    private String firstName;
    private String middleName;
    private String lastName;

    // Page 2: Contact
    private String email;
    private String otpCode; // Required for Page 2 OTP verification

    // Page 3: Credentials
    private String studentId; // Functions as username (10 digits)
    private String password;

    // Page 4: Address
    private String contactNumber;
    private String addressNo;
    private String addressStreet;
    private String addressBarangay;
    private String addressCity;
    private String addressProvince;

    // Page 5: Academic
    private String campusBranch;
    private String college;
    private String courseProgram;
    private byte[] userImage;
    private String userImageTag;

    // --- Constructor ---
    public User() {
        // Initialize all string fields to empty strings to avoid null issues
        this.firstName = "";
        this.middleName = "";
        this.lastName = "";
        this.email = "";
        this.otpCode = "";
        this.studentId = "";
        this.password = "";
        this.contactNumber = "";
        this.addressNo = "";
        this.addressStreet = "";
        this.addressBarangay = "";
        this.addressCity = "";
        this.addressProvince = "";
        this.campusBranch = "";
        this.college = "";
        this.courseProgram = "";
        this.userImageTag = "";
    }

    // --- Getters and Setters (Updated for completeness) ---

    public byte[] getUserImage() { return userImage; }
    public void setUserImage(byte[] profileImage) { this.userImage = profileImage; }

    public String getUserImageTag() {
        return userImageTag;
    }
    public void setUserImageTag(String imageTag) { this.userImageTag = imageTag; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddressNo() { return addressNo; }
    public void setAddressNo(String addressNo) { this.addressNo = addressNo; }

    public String getAddressStreet() { return addressStreet; }
    public void setAddressStreet(String addressStreet) { this.addressStreet = addressStreet; }

    public String getAddressBarangay() { return addressBarangay; }
    public void setAddressBarangay(String addressBarangay) { this.addressBarangay = addressBarangay; }

    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String addressCity) { this.addressCity = addressCity; }

    public String getAddressProvince() { return addressProvince; }
    public void setAddressProvince(String addressProvince) { this.addressProvince = addressProvince; }

    public String getCampusBranch() { return campusBranch; }
    public void setCampusBranch(String campusBranch) { this.campusBranch = campusBranch; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getCourseProgram() { return courseProgram; }
    public void setCourseProgram(String courseProgram) { this.courseProgram = courseProgram; }


    // --- Parcelable Implementation ---

    protected User(Parcel in) {
        firstName = in.readString();
        middleName = in.readString();
        lastName = in.readString();
        email = in.readString();
        otpCode = in.readString();
        studentId = in.readString();
        password = in.readString();
        contactNumber = in.readString();
        addressNo = in.readString();
        addressStreet = in.readString();
        addressBarangay = in.readString();
        addressCity = in.readString();
        addressProvince = in.readString();
        campusBranch = in.readString();
        college = in.readString();
        courseProgram = in.readString();
        userImage = in.createByteArray();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(middleName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(otpCode);
        dest.writeString(studentId);
        dest.writeString(password);
        dest.writeString(contactNumber);
        dest.writeString(addressNo);
        dest.writeString(addressStreet);
        dest.writeString(addressBarangay);
        dest.writeString(addressCity);
        dest.writeString(addressProvince);
        dest.writeString(campusBranch);
        dest.writeString(college);
        dest.writeString(courseProgram);
        dest.writeByteArray(userImage);
    }
}