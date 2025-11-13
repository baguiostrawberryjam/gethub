package com.example.gethub.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gethub.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ViewModel to manage data and validation across the 5-page registration flow.
 */
public class RegistrationViewModel extends ViewModel {
    // Current user object holding all data
    private final MutableLiveData<User> currentUser = new MutableLiveData<>(new User());

    // --- Validation Statuses (one for each page/group) ---
    private final MutableLiveData<Boolean> page1Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> page2Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> page3Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> page4Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> page5Valid = new MutableLiveData<>(false);

    // --- General Error Messages (Page 1) ---
    private final MutableLiveData<String> errFirstName = new MutableLiveData<>("");
    private final MutableLiveData<String> errLastName = new MutableLiveData<>("");

    // --- Page 2 LiveData (Email & OTP) ---
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private final MutableLiveData<String> enteredOTP = new MutableLiveData<>("");
    private final MutableLiveData<String> generatedOTP = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isOtpSent = new MutableLiveData<>(false);
    private final MutableLiveData<String> errEmail = new MutableLiveData<>("");
    private final MutableLiveData<String> errOTP = new MutableLiveData<>("");
    private final MutableLiveData<String> showOtpDialogEvent = new MutableLiveData<>(); // Event to trigger dialog

    // --- Page 3 LiveData (Credentials) ---
    private final MutableLiveData<String> confirmPassword = new MutableLiveData<>("");
    private final MutableLiveData<String> errStudentId = new MutableLiveData<>("");
    private final MutableLiveData<String> errPassword = new MutableLiveData<>("");
    private final MutableLiveData<String> errConfirmPassword = new MutableLiveData<>("");

    // --- Page 4 LiveData (Address) ---
    private final MutableLiveData<String> errContactNumber = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressNo = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressStreet = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressBarangay = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressCity = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressProvince = new MutableLiveData<>("");

    // --- Page 5 LiveData (Academic) ---
    private Map<String, List<String>> collegeCoursesMap = new HashMap<>();
    private final MutableLiveData<List<String>> availableCourses = new MutableLiveData<>();
    private final MutableLiveData<String> errCampusBranch = new MutableLiveData<>("");
    private final MutableLiveData<String> errCollege = new MutableLiveData<>("");
    private final MutableLiveData<String> errCourseProgram = new MutableLiveData<>("");

    // --- Navigation State ---
    private final MutableLiveData<Integer> navigateToPage = new MutableLiveData<>();
    private static final int TOTAL_PAGES = 5;

    public RegistrationViewModel() {
        super();
        // Initialize availableCourses with a default placeholder list
        availableCourses.setValue(new ArrayList<>(Arrays.asList("Select Course/Program")));
    }

    // --- Public LiveData Accessors ---

    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<Integer> getNavigateToPage() { return navigateToPage; }

    // Page Validity Accessors
    public LiveData<Boolean> getPage1Valid() { return page1Valid; }
    public LiveData<Boolean> getPage2Valid() { return page2Valid; }
    public LiveData<Boolean> getPage3Valid() { return page3Valid; }
    public LiveData<Boolean> getPage4Valid() { return page4Valid; }
    public LiveData<Boolean> getPage5Valid() { return page5Valid; }

    // Page 1 Error Accessors
    public LiveData<String> getErrFirstName() { return errFirstName; }
    public LiveData<String> getErrLastName() { return errLastName; }

    // Page 2 Accessors
    public LiveData<String> getEnteredOTP() { return enteredOTP; }
    public LiveData<Boolean> getIsOtpSent() { return isOtpSent; }
    public LiveData<String> getErrEmail() { return errEmail; }
    public LiveData<String> getErrOTP() { return errOTP; }
    public LiveData<String> getShowOtpDialogEvent() { return showOtpDialogEvent; }

    // Page 3 Accessors
    public LiveData<String> getConfirmPassword() { return confirmPassword; }
    public LiveData<String> getErrStudentId() { return errStudentId; }
    public LiveData<String> getErrPassword() { return errPassword; }
    public LiveData<String> getErrConfirmPassword() { return errConfirmPassword; }

    // Page 4 Accessors
    public LiveData<String> getErrContactNumber() { return errContactNumber; }
    public LiveData<String> getErrAddressNo() { return errAddressNo; }
    public LiveData<String> getErrAddressStreet() { return errAddressStreet; }
    public LiveData<String> getErrAddressBarangay() { return errAddressBarangay; }
    public LiveData<String> getErrAddressCity() { return errAddressCity; }
    public LiveData<String> getErrAddressProvince() { return errAddressProvince; }

    // Page 5 Accessors
    public LiveData<List<String>> getAvailableCourses() { return availableCourses; }
    public LiveData<String> getErrCampusBranch() { return errCampusBranch; }
    public LiveData<String> getErrCollege() { return errCollege; }
    public LiveData<String> getErrCourseProgram() { return errCourseProgram; }

    // --- Core Navigation Methods ---

    /** Navigates to the next page if the current page is valid. */
    public void goToNextPage(int currentPage) {
        Boolean isValid = false;

        // Determine if the current page is valid (use Boolean.TRUE.equals to handle null safety)
        if (currentPage == 0) isValid = Boolean.TRUE.equals(page1Valid.getValue());
        else if (currentPage == 1) isValid = Boolean.TRUE.equals(page2Valid.getValue());
        else if (currentPage == 2) isValid = Boolean.TRUE.equals(page3Valid.getValue());
        else if (currentPage == 3) isValid = Boolean.TRUE.equals(page4Valid.getValue());
        else if (currentPage == 4) isValid = Boolean.TRUE.equals(page5Valid.getValue());


        if (isValid) {
            if (currentPage < TOTAL_PAGES - 1) {
                // Navigate forward
                navigateToPage.setValue(currentPage + 1);
            } else {
                // Last page: Handle registration submission (placeholder)
                User user = currentUser.getValue();
                if (user != null) {
                    System.out.println("Registration Complete for Student ID: " + user.getStudentId());
                    // In a real app: call API, save user, and navigate to HomeActivity.
                    // For now, let's show a success message or intent to the next screen.
                }
            }
        } else {
            // Force validation check on current page to display missing errors immediately
            if (currentPage == 0) validatePage1();
            else if (currentPage == 1) validatePage2();
            else if (currentPage == 2) validatePage3();
            else if (currentPage == 3) validatePage4();
            else if (currentPage == 4) validatePage5();

            System.out.println("Page " + (currentPage + 1) + " is invalid. Cannot proceed.");
        }
    }

    /** Navigates to the previous page. */
    public void goToPreviousPage(int currentPage) {
        if (currentPage > 0) {
            navigateToPage.setValue(currentPage - 1);
        }
    }

    /** Helper to clear navigation flag after consumption. */
    public void onNavigationComplete() {
        navigateToPage.setValue(null);
    }

    // --- Page 1 Validation Logic (Names) ---

    public void onFirstNameChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setFirstName(value);
            currentUser.setValue(user);
            validatePage1();
        }
    }

    public void onMiddleNameChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setMiddleName(value);
            currentUser.setValue(user);
        }
    }

    public void onLastNameChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setLastName(value);
            currentUser.setValue(user);
            validatePage1();
        }
    }

    private void validatePage1() {
        User user = currentUser.getValue();
        boolean valid = true;

        if (user == null) {
            page1Valid.setValue(false);
            return;
        }

        // First Name Validation (Required)
        if (user.getFirstName().trim().isEmpty()) {
            errFirstName.setValue("First name is required.");
            valid = false;
        } else {
            errFirstName.setValue("");
        }

        // Last Name Validation (Required)
        if (user.getLastName().trim().isEmpty()) {
            errLastName.setValue("Last name is required.");
            valid = false;
        } else {
            errLastName.setValue("");
        }

        page1Valid.setValue(valid);
    }

    // --- Page 2 Validation and OTP Logic (Email & OTP) ---

    public void onEmailChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            // Reset OTP status and generated code if email changes after being sent
            if (Boolean.TRUE.equals(isOtpSent.getValue())) {
                isOtpSent.setValue(false);
                generatedOTP.setValue("");
                enteredOTP.setValue("");
            }
            user.setEmail(value.trim());
            currentUser.setValue(user);
            validatePage2();
        }
    }

    public void onOTPChanged(String value) {
        enteredOTP.setValue(value.trim());
        validatePage2();
    }

    /** Simulates generating a 6-digit OTP and triggers the Activity to show it. */
    public void generateAndSendOtp() {
        // 1. Validate email format before sending
        if (currentUser.getValue() == null || !isValidEmail(currentUser.getValue().getEmail())) {
            errEmail.setValue("Please enter a valid email address before sending the code.");
            isOtpSent.setValue(false);
            return;
        }

        // 2. Generate a new 6-digit code
        Random random = new Random();
        String newOtp = String.format("%06d", random.nextInt(1000000));
        generatedOTP.setValue(newOtp);

        // 3. Update status and trigger the dialog in the Activity
        isOtpSent.setValue(true);
        showOtpDialogEvent.setValue(newOtp); // Triggers the AlertDialog in RegistrationActivity
        errEmail.setValue(""); // Clear any previous email format error
        errOTP.setValue("Code sent! Please check the dialog above.");
    }

    /** Helper to clear the OTP dialog event after the Activity handles it. */
    public void onOtpDialogEventConsumed() {
        showOtpDialogEvent.setValue(null);
    }

    private boolean isValidEmail(String email) {
        return email != null && emailPattern.matcher(email).matches();
    }

    private void validatePage2() {
        User user = currentUser.getValue();
        boolean valid = true;

        if (user == null) {
            page2Valid.setValue(false);
            return;
        }

        // Email Validation
        if (user.getEmail().isEmpty()) {
            errEmail.setValue("Email is required.");
            valid = false;
        } else if (!isValidEmail(user.getEmail())) {
            errEmail.setValue("Invalid email format.");
            valid = false;
        } else {
            errEmail.setValue("");
        }

        // OTP Validation
        if (!Boolean.TRUE.equals(isOtpSent.getValue())) {
            errOTP.setValue("Please send and confirm the OTP.");
            valid = false;
        } else if (enteredOTP.getValue() == null || enteredOTP.getValue().isEmpty()) {
            errOTP.setValue("Please enter the 6-digit code.");
            valid = false;
        } else if (!enteredOTP.getValue().equals(generatedOTP.getValue())) {
            errOTP.setValue("The entered code is incorrect.");
            valid = false;
        } else {
            errOTP.setValue("Email verified successfully!"); // Success feedback
        }

        page2Valid.setValue(valid && errOTP.getValue().contains("success"));
    }


    // --- Page 3 Validation Logic (Credentials) ---

    public void onStudentIdChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setStudentId(value);
            currentUser.setValue(user);
            validatePage3();
        }
    }

    public void onPasswordChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setPassword(value);
            currentUser.setValue(user);
            validatePage3();
        }
    }

    public void onConfirmPasswordChanged(String value) {
        confirmPassword.setValue(value);
        validatePage3();
    }

    private void validatePage3() {
        User user = currentUser.getValue();
        boolean valid = true;

        if (user == null) {
            page3Valid.setValue(false);
            return;
        }

        // Student ID Validation (10 digits)
        if (user.getStudentId().trim().length() != 10) {
            errStudentId.setValue("Student ID must be 10 digits.");
            valid = false;
        } else {
            errStudentId.setValue("");
        }

        // Password Validation (at least 8 chars)
        if (user.getPassword().length() < 8) {
            errPassword.setValue("Password must be at least 8 characters.");
            valid = false;
        } else {
            errPassword.setValue("");
        }

        // Confirm Password Validation
        if (!user.getPassword().equals(confirmPassword.getValue())) {
            errConfirmPassword.setValue("Passwords do not match.");
            valid = false;
        } else {
            errConfirmPassword.setValue("");
        }

        page3Valid.setValue(valid);
    }

    // --- Page 4 Validation Logic (Address) ---

    public void onContactNumberChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setContactNumber(value);
            currentUser.setValue(user);
            validatePage4();
        }
    }

    public void onAddressNoChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setAddressNo(value);
            currentUser.setValue(user);
            validatePage4();
        }
    }

    public void onAddressStreetChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setAddressStreet(value);
            currentUser.setValue(user);
            validatePage4();
        }
    }

    public void onAddressBarangayChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setAddressBarangay(value);
            currentUser.setValue(user);
            validatePage4();
        }
    }

    public void onAddressCityChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setAddressCity(value);
            currentUser.setValue(user);
            validatePage4();
        }
    }

    public void onAddressProvinceChanged(String value) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setAddressProvince(value);
            currentUser.setValue(user);
            validatePage4();
        }
    }

    private void validatePage4() {
        User user = currentUser.getValue();
        boolean valid = true;

        if (user == null) {
            page4Valid.setValue(false);
            return;
        }

        // Contact Number Validation (e.g., PH format: 11 digits starting with 09)
        Pattern contactPattern = Pattern.compile("^09\\d{9}$");
        if (user.getContactNumber().trim().isEmpty()) {
            errContactNumber.setValue("Contact number is required.");
            valid = false;
        } else if (!contactPattern.matcher(user.getContactNumber().trim()).matches()) {
            errContactNumber.setValue("Enter a valid 11-digit mobile number (e.g., 09123456789).");
            valid = false;
        } else {
            errContactNumber.setValue("");
        }

        // Address No Validation (Required)
        if (user.getAddressNo().trim().isEmpty()) {
            errAddressNo.setValue("House/Bldg No. is required.");
            valid = false;
        } else {
            errAddressNo.setValue("");
        }

        // Street Validation (Required)
        if (user.getAddressStreet().trim().isEmpty()) {
            errAddressStreet.setValue("Street is required.");
            valid = false;
        } else {
            errAddressStreet.setValue("");
        }

        // Barangay Validation (Required)
        if (user.getAddressBarangay().trim().isEmpty()) {
            errAddressBarangay.setValue("Barangay is required.");
            valid = false;
        } else {
            errAddressBarangay.setValue("");
        }

        // City Validation (Required)
        if (user.getAddressCity().trim().isEmpty()) {
            errAddressCity.setValue("City/Municipality is required.");
            valid = false;
        } else {
            errAddressCity.setValue("");
        }

        // Province Validation (Required)
        if (user.getAddressProvince().trim().isEmpty()) {
            errAddressProvince.setValue("Province is required.");
            valid = false;
        } else {
            errAddressProvince.setValue("");
        }


        page4Valid.setValue(valid);
    }

    // --- Page 5 Validation Logic (Academic) ---

    public void setCoursesForCollege(String collegeName, List<String> courses) {
        collegeCoursesMap.put(collegeName, courses);
    }

    public void onCampusBranchSelected(String campus) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setCampusBranch(campus);
            currentUser.setValue(user);
            validatePage5();
        }
    }

    public void onCollegeSelected(String college) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setCollege(college);
            // Reset course if college changes
            user.setCourseProgram("");
            currentUser.setValue(user);

            // Update available courses
            if (collegeCoursesMap.containsKey(college)) {
                availableCourses.setValue(collegeCoursesMap.get(college));
            } else {
                availableCourses.setValue(new ArrayList<>(Arrays.asList("Select Course/Program")));
            }
            validatePage5();
        }
    }

    public void onCourseProgramSelected(String course) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setCourseProgram(course);
            currentUser.setValue(user);
            validatePage5();
        }
    }

    private void validatePage5() {
        User user = currentUser.getValue();
        boolean valid = true;

        if (user == null) {
            page5Valid.setValue(false);
            return;
        }

        if (user.getCampusBranch().isEmpty()) {
            errCampusBranch.setValue("Campus branch is required.");
            valid = false;
        } else {
            errCampusBranch.setValue("");
        }

        if (user.getCollege().isEmpty()) {
            errCollege.setValue("College is required.");
            valid = false;
        } else {
            errCollege.setValue("");
        }

        if (user.getCourseProgram().isEmpty() || user.getCourseProgram().equals("Select Course/Program")) {
            errCourseProgram.setValue("Course/Program is required.");
            valid = false;
        } else {
            errCourseProgram.setValue("");
        }

        page5Valid.setValue(valid);
    }
}
