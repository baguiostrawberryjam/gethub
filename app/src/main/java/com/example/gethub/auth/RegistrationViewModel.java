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

public class RegistrationViewModel extends ViewModel {
    private final MutableLiveData<User> currentUser = new MutableLiveData<>(new User());

    private final MutableLiveData<Boolean> page1Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> page2Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> page3Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> page4Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> page5Valid = new MutableLiveData<>(false);

    private final MutableLiveData<String> errFirstName = new MutableLiveData<>("");
    private final MutableLiveData<String> errLastName = new MutableLiveData<>("");

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private final MutableLiveData<String> enteredOTP = new MutableLiveData<>("");
    private final MutableLiveData<String> generatedOTP = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isOtpSent = new MutableLiveData<>(false);
    private final MutableLiveData<String> errEmail = new MutableLiveData<>("");
    private final MutableLiveData<String> errOTP = new MutableLiveData<>("");
    private final MutableLiveData<String> showOtpDialogEvent = new MutableLiveData<>();

    private final MutableLiveData<String> confirmPassword = new MutableLiveData<>("");
    private final MutableLiveData<String> errStudentId = new MutableLiveData<>("");
    private final MutableLiveData<String> errPassword = new MutableLiveData<>("");
    private final MutableLiveData<String> errConfirmPassword = new MutableLiveData<>("");
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])" +              // at least one digit
                    "(?=.*[a-z])" +               // at least one lowercase letter
                    "(?=.*[A-Z])" +               // at least one uppercase letter
                    "(?=.*[!@#$%^&*()\\-_=+\\[\\]{}|:;\"'<>,./?~`])" + // at least one special character
                    "(?=\\S+$)" +                 // no whitespace
                    ".{8,}$";                     // at least 8 characters
    private final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);

    private final MutableLiveData<String> errContactNumber = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressNo = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressStreet = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressBarangay = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressCity = new MutableLiveData<>("");
    private final MutableLiveData<String> errAddressProvince = new MutableLiveData<>("");

    private Map<String, List<String>> collegeCoursesMap = new HashMap<>();
    private final MutableLiveData<List<String>> availableCourses = new MutableLiveData<>();
    private final MutableLiveData<String> errCampusBranch = new MutableLiveData<>("");
    private final MutableLiveData<String> errCollege = new MutableLiveData<>("");
    private final MutableLiveData<String> errCourseProgram = new MutableLiveData<>("");

    private final MutableLiveData<Integer> navigateToPage = new MutableLiveData<>();
    private final MutableLiveData<User> registrationCompleteEvent = new MutableLiveData<>();
    private static final int TOTAL_PAGES = 5;

    public RegistrationViewModel() {
        super();
        availableCourses.setValue(new ArrayList<>(Arrays.asList("Select Course/Program")));
    }

    public void updateProfileImage(byte[] imageBytes) {
        currentUser.getValue().setUserImage(imageBytes);
        currentUser.getValue().setUserImageTag("image_selected");
        validatePage3();
    }

    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<Integer> getNavigateToPage() { return navigateToPage; }
    public LiveData<User> getRegistrationCompleteEvent() { return registrationCompleteEvent; }

    public LiveData<Boolean> getPage1Valid() { return page1Valid; }
    public LiveData<Boolean> getPage2Valid() { return page2Valid; }
    public LiveData<Boolean> getPage3Valid() { return page3Valid; }
    public LiveData<Boolean> getPage4Valid() { return page4Valid; }
    public LiveData<Boolean> getPage5Valid() { return page5Valid; }

    public LiveData<String> getErrFirstName() { return errFirstName; }
    public LiveData<String> getErrLastName() { return errLastName; }

    public LiveData<String> getEnteredOTP() { return enteredOTP; }
    public LiveData<Boolean> getIsOtpSent() { return isOtpSent; }
    public LiveData<String> getErrEmail() { return errEmail; }
    public LiveData<String> getErrOTP() { return errOTP; }
    public LiveData<String> getShowOtpDialogEvent() { return showOtpDialogEvent; }

    public LiveData<String> getConfirmPassword() { return confirmPassword; }
    public LiveData<String> getErrStudentId() { return errStudentId; }
    public LiveData<String> getErrPassword() { return errPassword; }
    public LiveData<String> getErrConfirmPassword() { return errConfirmPassword; }

    public LiveData<String> getErrContactNumber() { return errContactNumber; }
    public LiveData<String> getErrAddressNo() { return errAddressNo; }
    public LiveData<String> getErrAddressStreet() { return errAddressStreet; }
    public LiveData<String> getErrAddressBarangay() { return errAddressBarangay; }
    public LiveData<String> getErrAddressCity() { return errAddressCity; }
    public LiveData<String> getErrAddressProvince() { return errAddressProvince; }

    public LiveData<List<String>> getAvailableCourses() { return availableCourses; }
    public LiveData<String> getErrCampusBranch() { return errCampusBranch; }
    public LiveData<String> getErrCollege() { return errCollege; }
    public LiveData<String> getErrCourseProgram() { return errCourseProgram; }

    public void goToNextPage(int currentPage) {
        boolean pageIsValid = false;
        switch (currentPage) {
            case 0: pageIsValid = isPage1Valid(); if (!pageIsValid) validatePage1(); break;
            case 1: pageIsValid = isPage2Valid(); if (!pageIsValid) validatePage2(); break;
            case 2: pageIsValid = isPage3Valid(); if (!pageIsValid) validatePage3(); break;
            case 3: pageIsValid = isPage4Valid(); if (!pageIsValid) validatePage4(); break;
            case 4: pageIsValid = isPage5Valid(); if (!pageIsValid) validatePage5(); break;
        }

        if (pageIsValid) {
            if (currentPage < TOTAL_PAGES - 1) {
                navigateToPage.setValue(currentPage + 1);
            } else {
                registrationCompleteEvent.setValue(currentUser.getValue());
            }
        } else {
            System.out.println("Page " + (currentPage + 1) + " is invalid.");
        }
    }

    public void goToPreviousPage(int currentPage) { if (currentPage > 0) navigateToPage.setValue(currentPage - 1); }
    public void onNavigationComplete() { navigateToPage.setValue(null); }
    public void onRegistrationComplete() { registrationCompleteEvent.setValue(null); }

    // Page 1: Names
    public void onFirstNameChanged(String value) { User u = currentUser.getValue(); if(u!=null){ u.setFirstName(value); currentUser.setValue(u); if(!value.trim().isEmpty()) errFirstName.setValue(""); page1Valid.setValue(isPage1Valid()); } }
    public void onMiddleNameChanged(String value) { if(currentUser.getValue()!=null) currentUser.getValue().setMiddleName(value); }
    public void onLastNameChanged(String value) { User u = currentUser.getValue(); if(u!=null){ u.setLastName(value); currentUser.setValue(u); if(!value.trim().isEmpty()) errLastName.setValue(""); page1Valid.setValue(isPage1Valid()); } }
    private boolean isPage1Valid() { User u = currentUser.getValue(); return u != null && !u.getFirstName().trim().isEmpty() && !u.getLastName().trim().isEmpty(); }
    private void validatePage1() { if(currentUser.getValue().getFirstName().trim().isEmpty()) errFirstName.setValue("First name is required."); if(currentUser.getValue().getLastName().trim().isEmpty()) errLastName.setValue("Last name is required."); }

    // Page 2: Email & OTP
    public void onEmailChanged(String value) {
        User user = currentUser.getValue(); if (user == null) return;
        if (Boolean.TRUE.equals(isOtpSent.getValue())) { isOtpSent.setValue(false); generatedOTP.setValue(""); enteredOTP.setValue(""); }
        user.setEmail(value.trim()); currentUser.setValue(user);
        if (!value.trim().isEmpty()) errEmail.setValue(isValidEmail(value.trim()) ? "" : "Invalid email format.");
        page2Valid.setValue(isPage2Valid());
    }
    public void onOTPChanged(String value) {
        enteredOTP.setValue(value.trim());
        if (Boolean.TRUE.equals(isOtpSent.getValue()) && !value.trim().isEmpty()) {
            errOTP.setValue(value.trim().equals(generatedOTP.getValue()) ? "Email verified successfully!" : "The entered code is incorrect.");
        }
        page2Valid.setValue(isPage2Valid());
    }
    public void generateAndSendOtp() {
        if (currentUser.getValue() == null || !isValidEmail(currentUser.getValue().getEmail())) { errEmail.setValue("Please enter a valid email address first."); isOtpSent.setValue(false); return; }
        String newOtp = String.format("%06d", new Random().nextInt(1000000));
        generatedOTP.setValue(newOtp); isOtpSent.setValue(true); showOtpDialogEvent.setValue(newOtp);
        errEmail.setValue(""); errOTP.setValue("Code sent! Please check the dialog above.");
    }
    public void onOtpDialogEventConsumed() { showOtpDialogEvent.setValue(null); }
    private boolean isValidEmail(String email) { return email != null && emailPattern.matcher(email).matches(); }
    private boolean isPage2Valid() { return isValidEmail(currentUser.getValue().getEmail()) && Boolean.TRUE.equals(isOtpSent.getValue()) && enteredOTP.getValue() != null && enteredOTP.getValue().equals(generatedOTP.getValue()); }
    private void validatePage2() {
        if (currentUser.getValue().getEmail().isEmpty()) errEmail.setValue("Email is required.");
        else if (!isValidEmail(currentUser.getValue().getEmail())) errEmail.setValue("Invalid email format.");
        if (!Boolean.TRUE.equals(isOtpSent.getValue())) errOTP.setValue("Please send and confirm the OTP.");
        else if (enteredOTP.getValue().isEmpty()) errOTP.setValue("Please enter the 6-digit code.");
    }

    // Page 3: Credentials
    public void onStudentIdChanged(String value) { User u=currentUser.getValue(); if(u!=null){ u.setStudentId(value); currentUser.setValue(u); if (!value.trim().isEmpty() && value.trim().length() != 10) errStudentId.setValue("Student ID must be 10 digits."); else errStudentId.setValue(""); page3Valid.setValue(isPage3Valid()); } }
    public void onPasswordChanged(String value) {
        User user = currentUser.getValue(); if (user == null) return;
        user.setPassword(value); currentUser.setValue(user);
        if (!value.isEmpty()) {
            if (value.length() < 8) {
                errPassword.setValue("Password must be at least 8 characters.");
            } else if (!passwordPattern.matcher(value).matches()) {
                errPassword.setValue("Must have uppercase, lowercase, number, & special character.");
            } else {
                errPassword.setValue("");
            }
        }
        if (confirmPassword.getValue() != null && !confirmPassword.getValue().isEmpty()) {
            errConfirmPassword.setValue(value.equals(confirmPassword.getValue()) ? "" : "Passwords do not match.");
        }
        page3Valid.setValue(isPage3Valid());
    }
    public void onConfirmPasswordChanged(String value) {
        confirmPassword.setValue(value);
        if (!value.isEmpty()) {
            errConfirmPassword.setValue(value.equals(currentUser.getValue().getPassword()) ? "" : "Passwords do not match.");
        } else {
             errConfirmPassword.setValue("");
        }
        page3Valid.setValue(isPage3Valid());
    }
    private boolean isPage3Valid() {
        User u = currentUser.getValue();
        // Check if U is not null, then check the tag safely
        boolean isImageSelected = u != null && "image_selected".equals(u.getUserImageTag());

        return isImageSelected
                && u.getStudentId().trim().length() == 10
                && passwordPattern.matcher(u.getPassword()).matches()
                && u.getPassword().equals(confirmPassword.getValue());
    }
    private void validatePage3() {
        if (currentUser.getValue().getStudentId().trim().isEmpty()) errStudentId.setValue("Student ID is required.");
        onPasswordChanged(currentUser.getValue().getPassword());
        if (confirmPassword.getValue().isEmpty()) errConfirmPassword.setValue("Please confirm your password.");

    }

    // Page 4: Address
    public void onContactNumberChanged(String v) { User u=currentUser.getValue(); if(u!=null) { u.setContactNumber(v); if(!v.trim().isEmpty()){Pattern p=Pattern.compile("^09\\d{9}$"); errContactNumber.setValue(p.matcher(v.trim()).matches()?"":"Enter a valid 11-digit mobile number.");} else {errContactNumber.setValue("");} page4Valid.setValue(isPage4Valid()); } }
    public void onAddressNoChanged(String v) { User u=currentUser.getValue(); if(u!=null) { u.setAddressNo(v); if(!v.trim().isEmpty()) errAddressNo.setValue(""); page4Valid.setValue(isPage4Valid()); } }
    public void onAddressStreetChanged(String v) { User u=currentUser.getValue(); if(u!=null) { u.setAddressStreet(v); if(!v.trim().isEmpty()) errAddressStreet.setValue(""); page4Valid.setValue(isPage4Valid()); } }
    public void onAddressBarangayChanged(String v) { User u=currentUser.getValue(); if(u!=null) { u.setAddressBarangay(v); if(!v.trim().isEmpty()) errAddressBarangay.setValue(""); page4Valid.setValue(isPage4Valid()); } }
    public void onAddressCityChanged(String v) { User u=currentUser.getValue(); if(u!=null) { u.setAddressCity(v); if(!v.trim().isEmpty()) errAddressCity.setValue(""); page4Valid.setValue(isPage4Valid()); } }
    public void onAddressProvinceChanged(String v) { User u=currentUser.getValue(); if(u!=null) { u.setAddressProvince(v); if(!v.trim().isEmpty()) errAddressProvince.setValue(""); page4Valid.setValue(isPage4Valid()); } }
    private boolean isPage4Valid() { User u = currentUser.getValue(); if (u == null) return false; Pattern p = Pattern.compile("^09\\d{9}$"); return p.matcher(u.getContactNumber().trim()).matches() && !u.getAddressNo().trim().isEmpty() && !u.getAddressStreet().trim().isEmpty() && !u.getAddressBarangay().trim().isEmpty() && !u.getAddressCity().trim().isEmpty() && !u.getAddressProvince().trim().isEmpty(); }
    private void validatePage4() { User u = currentUser.getValue(); if(u.getContactNumber().trim().isEmpty()) errContactNumber.setValue("Contact number is required."); if(u.getAddressNo().trim().isEmpty()) errAddressNo.setValue("House/Bldg No. is required."); if(u.getAddressStreet().trim().isEmpty()) errAddressStreet.setValue("Street is required."); if(u.getAddressBarangay().trim().isEmpty()) errAddressBarangay.setValue("Barangay is required."); if(u.getAddressCity().trim().isEmpty()) errAddressCity.setValue("City/Municipality is required."); if(u.getAddressProvince().trim().isEmpty()) errAddressProvince.setValue("Province is required."); }

    // Page 5: Academics
    public void setCoursesForCollege(String c, List<String> co) { collegeCoursesMap.put(c, co); }
    public void onCampusBranchSelected(String c) { User u=currentUser.getValue(); if(u!=null){u.setCampusBranch(c); currentUser.setValue(u); if(!c.isEmpty()) errCampusBranch.setValue(""); page5Valid.setValue(isPage5Valid()); } }
    public void onCollegeSelected(String c) { User u=currentUser.getValue(); if(u!=null){u.setCollege(c); u.setCourseProgram(""); currentUser.setValue(u); if(!c.isEmpty()) errCollege.setValue(""); List<String> co=collegeCoursesMap.get(c); availableCourses.setValue(co!=null&&!co.isEmpty()?co:new ArrayList<>(Arrays.asList("Select Course/Program"))); page5Valid.setValue(isPage5Valid()); } }
    public void onCourseProgramSelected(String c) { User u=currentUser.getValue(); if(u!=null){u.setCourseProgram(c); currentUser.setValue(u); if(!c.isEmpty()&&!c.equals("Select Course/Program")) errCourseProgram.setValue(""); page5Valid.setValue(isPage5Valid()); } }
    private boolean isPage5Valid() { User u = currentUser.getValue(); return u != null && !u.getCampusBranch().isEmpty() && !u.getCollege().isEmpty() && !u.getCourseProgram().isEmpty() && !u.getCourseProgram().equals("Select Course/Program"); }
    private void validatePage5() { User u = currentUser.getValue(); if(u.getCampusBranch().isEmpty()) errCampusBranch.setValue("Campus branch is required."); if(u.getCollege().isEmpty()) errCollege.setValue("College is required."); if(u.getCourseProgram().isEmpty() || u.getCourseProgram().equals("Select Course/Program")) errCourseProgram.setValue("Course/Program is required."); }
}
