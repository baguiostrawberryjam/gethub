package com.example.gethub.auth;

import android.util.Patterns;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.example.gethub.auth.AuthRepository.User;
import java.util.List;
import java.util.regex.Pattern;

public class RegistrationViewModel extends ViewModel {

    // Use the singleton instance
    private final AuthRepository authRepository = AuthRepository.getInstance();

    // LiveData for registration result and page control
    private final MutableLiveData<Boolean> registrationResult = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);

    // --- Form Fields ---
    public final MutableLiveData<String> firstName = new MutableLiveData<>("");
    public final MutableLiveData<String> middleName = new MutableLiveData<>("");
    public final MutableLiveData<String> lastName = new MutableLiveData<>("");
    public final MutableLiveData<String> email = new MutableLiveData<>("");
    public final MutableLiveData<String> studentId = new MutableLiveData<>("");
    public final MutableLiveData<String> password = new MutableLiveData<>("");
    public final MutableLiveData<String> confirmPassword = new MutableLiveData<>("");
    public final MutableLiveData<String> contactNumber = new MutableLiveData<>(""); // Will be combined with +63 in the view
    public final MutableLiveData<String> address = new MutableLiveData<>("");
    public final MutableLiveData<String> campusBranch = new MutableLiveData<>("");
    public final MutableLiveData<String> college = new MutableLiveData<>("");
    public final MutableLiveData<String> course = new MutableLiveData<>("");
    public final MutableLiveData<String> yearLevel = new MutableLiveData<>("");

    // --- Dynamic Data for Spinners ---
    public final LiveData<List<String>> availableCourses = Transformations.map(college, 
        SchoolDataProvider::getCoursesForCollege
    );

    // --- Validation Errors ---
    public final MutableLiveData<String> firstNameError = new MutableLiveData<>(null);
    public final MutableLiveData<String> lastNameError = new MutableLiveData<>(null);
    public final MutableLiveData<String> emailError = new MutableLiveData<>(null);
    public final MutableLiveData<String> studentIdError = new MutableLiveData<>(null);
    public final MutableLiveData<String> passwordError = new MutableLiveData<>(null);
    public final MutableLiveData<String> confirmPasswordError = new MutableLiveData<>(null);
    public final MutableLiveData<String> contactNumberError = new MutableLiveData<>(null);
    public final MutableLiveData<String> addressError = new MutableLiveData<>(null);
    public final MutableLiveData<String> campusBranchError = new MutableLiveData<>(null);
    public final MutableLiveData<String> collegeError = new MutableLiveData<>(null);
    public final MutableLiveData<String> courseError = new MutableLiveData<>(null);
    public final MutableLiveData<String> yearLevelError = new MutableLiveData<>(null);

    // Updated to include the dot (.) as a special character
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!.]).*$");

    public LiveData<Boolean> getRegistrationResult() { return registrationResult; }
    public LiveData<Integer> getCurrentPage() { return currentPage; }

    public void nextPage() {
        if (validatePage(currentPage.getValue())) {
            currentPage.setValue(currentPage.getValue() + 1);
        }
    }

    public void previousPage() {
        currentPage.setValue(currentPage.getValue() - 1);
    }

    private boolean validatePage(int page) {
        switch (page) {
            case 1: return validatePage1();
            case 2: return validatePage2();
            case 3: return validatePage3();
            case 4: return validatePage4();
            case 5: return validatePage5();
            default: return true;
        }
    }

    private boolean validatePage1() {
        boolean isValid = true;
        if (firstName.getValue() == null || firstName.getValue().trim().isEmpty()) {
            firstNameError.setValue("First name is required");
            isValid = false;
        } else {
            firstNameError.setValue(null);
        }
        if (lastName.getValue() == null || lastName.getValue().trim().isEmpty()) {
            lastNameError.setValue("Last name is required");
            isValid = false;
        } else {
            lastNameError.setValue(null);
        }
        return isValid;
    }

    private boolean validatePage2() {
        boolean isValid = true;
        if (email.getValue() == null || email.getValue().trim().isEmpty()) {
            emailError.setValue("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getValue()).matches()) {
            emailError.setValue("Invalid email format");
            isValid = false;
        } else {
            emailError.setValue(null);
        }
        return isValid;
    }

    private boolean validatePage3() {
        boolean isValid = true;
        if (studentId.getValue() == null || !studentId.getValue().matches("\\d{10}")) {
            studentIdError.setValue("Student ID must be 10 digits");
            isValid = false;
        } else {
            studentIdError.setValue(null);
        }
        if (password.getValue() == null || password.getValue().isEmpty()) {
            passwordError.setValue("Password is required");
            isValid = false;
        } else if (!PASSWORD_PATTERN.matcher(password.getValue()).matches()) {
            passwordError.setValue("Must contain an uppercase, number, and special character");
            isValid = false;
        } else {
            passwordError.setValue(null);
        }
        if (confirmPassword.getValue() == null || !confirmPassword.getValue().equals(password.getValue())) {
            confirmPasswordError.setValue("Passwords do not match");
            isValid = false;
        } else {
            confirmPasswordError.setValue(null);
        }
        return isValid;
    }

    private boolean validatePage4() {
        boolean isValid = true;
        // Updated Contact Number Validation
        if (contactNumber.getValue() == null || !contactNumber.getValue().matches("\\d{10}")) {
            contactNumberError.setValue("Must be 10 digits");
            isValid = false;
        } else {
            contactNumberError.setValue(null);
        }
        if (address.getValue() == null || address.getValue().trim().isEmpty()) {
            addressError.setValue("Address is required");
            isValid = false;
        } else {
            addressError.setValue(null);
        }
        return isValid;
    }

    private boolean validatePage5() {
        boolean isValid = true;
        if (campusBranch.getValue() == null || campusBranch.getValue().isEmpty() || campusBranch.getValue().equals("Select a Campus Branch")) {
            campusBranchError.setValue("Campus branch is required");
            isValid = false;
        } else {
            campusBranchError.setValue(null);
        }
        if (college.getValue() == null || college.getValue().isEmpty() || college.getValue().equals("Select a College")) {
            collegeError.setValue("College is required");
            isValid = false;
        } else {
            collegeError.setValue(null);
        }
        if (course.getValue() == null || course.getValue().isEmpty() || course.getValue().equals("Select a Course/Program")) {
            courseError.setValue("Course/Program is required");
            isValid = false;
        } else {
            courseError.setValue(null);
        }
        if (yearLevel.getValue() == null || yearLevel.getValue().trim().isEmpty()) {
            yearLevelError.setValue("Year level is required");
            isValid = false;
        } else {
            yearLevelError.setValue(null);
        }
        return isValid;
    }

    public void registerUser() {
        boolean pagesValid = validatePage1() && validatePage2() && validatePage3() && validatePage4() && validatePage5();
        if (pagesValid) {
            // Add +63 back before creating the user object
            String fullContactNumber = "+63" + contactNumber.getValue();
            User newUser = new User(
                studentId.getValue(), password.getValue(), firstName.getValue(), middleName.getValue(),
                lastName.getValue(), email.getValue(), fullContactNumber, address.getValue(),
                campusBranch.getValue(), college.getValue(), course.getValue(), yearLevel.getValue()
            );
            boolean success = authRepository.register(newUser);
            registrationResult.setValue(success);
        } else {
            registrationResult.setValue(false);
        }
    }
}
