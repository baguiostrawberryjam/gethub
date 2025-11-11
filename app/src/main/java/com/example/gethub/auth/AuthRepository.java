package com.example.gethub.auth;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    // 1. The single, static instance of the repository
    private static volatile AuthRepository INSTANCE;

    private final Map<String, User> users = new HashMap<>();

    // 2. Private constructor to prevent anyone else from creating a new one
    private AuthRepository() {
        User testUser = new User("1234567890", "Password@1", "Test", "", "User", "test@example.com", "+639123456789", "Test Address", "Main Campus", "College of Engineering", "Bachelor of Science in Computer Engineering", "3");
        users.put(testUser.getStudentId(), testUser);
    }

    // 3. The public method to get the single instance
    public static AuthRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (AuthRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuthRepository();
                }
            }
        }
        return INSTANCE;
    }

    public User login(String studentId, String password) {
        if (users.containsKey(studentId)) {
            User user = users.get(studentId);
            if (user != null && user.getPassword().equals(password)) {
                return user; // Return the User object on success
            }
        }
        return null; // Return null on failure
    }

    public boolean register(User newUser) {
        if (users.containsKey(newUser.getStudentId())) {
            return false;
        }
        users.put(newUser.getStudentId(), newUser);
        return true;
    }

    // User class (Parcelable implementation is unchanged)
    public static class User implements Parcelable {
        private final String studentId, password, firstName, middleName, lastName, email, contactNumber, address, campusBranch, college, course, yearLevel;

        public User(String studentId, String password, String firstName, String middleName, String lastName, String email, String contactNumber, String address, String campusBranch, String college, String course, String yearLevel) {
            this.studentId = studentId; this.password = password; this.firstName = firstName; this.middleName = middleName; this.lastName = lastName; this.email = email; this.contactNumber = contactNumber; this.address = address; this.campusBranch = campusBranch; this.college = college; this.course = course; this.yearLevel = yearLevel;
        }

        protected User(Parcel in) {
            studentId = in.readString(); password = in.readString(); firstName = in.readString(); middleName = in.readString(); lastName = in.readString(); email = in.readString(); contactNumber = in.readString(); address = in.readString(); campusBranch = in.readString(); college = in.readString(); course = in.readString(); yearLevel = in.readString();
        }

        public static final Creator<User> CREATOR = new Creator<User>() {
            @Override
            public User createFromParcel(Parcel in) { return new User(in); }
            @Override
            public User[] newArray(int size) { return new User[size]; }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(studentId); dest.writeString(password); dest.writeString(firstName); dest.writeString(middleName); dest.writeString(lastName); dest.writeString(email); dest.writeString(contactNumber); dest.writeString(address); dest.writeString(campusBranch); dest.writeString(college); dest.writeString(course); dest.writeString(yearLevel);
        }

        @Override
        public int describeContents() { return 0; }

        // Getters
        public String getStudentId() { return studentId; }
        public String getPassword() { return password; }
        public String getFirstName() { return firstName; }
        public String getMiddleName() { return middleName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getContactNumber() { return contactNumber; }
        public String getAddress() { return address; }
        public String getCampusBranch() { return campusBranch; }
        public String getCollege() { return college; }
        public String getCourse() { return course; }
        public String getYearLevel() { return yearLevel; }
    }
}
