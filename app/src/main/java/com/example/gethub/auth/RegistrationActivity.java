package com.example.gethub.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import com.example.gethub.R;
import java.util.Arrays;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private RegistrationViewModel viewModel;
    private List<LinearLayout> pages;

    // UI Components
    private Button btnPrevious, btnNext, btnSubmit;
    private Spinner spCampusBranch, spCollege, spCourse;
    private EditText etFirstName, etMiddleName, etLastName, etEmail, etStudentIdReg, etPasswordReg, etConfirmPassword, etContactNumber, etAddress, etYearLevel;
    private TextView tvFirstNameError, tvLastNameError, tvEmailError, tvStudentIdError, tvPasswordError, tvConfirmPasswordError, tvContactNumberError, tvAddressError, tvCampusBranchError, tvCollegeError, tvCourseError, tvYearLevelError;
    
    // Adapter for the dynamic course spinner
    private ArrayAdapter<String> courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        initializeViews();
        setupSpinners();
        setupNavigation();
        observeViewModel();
        bindViewsToViewModel();
    }

    private void initializeViews() {
        pages = Arrays.asList(findViewById(R.id.llPage1), findViewById(R.id.llPage2), findViewById(R.id.llPage3), findViewById(R.id.llPage4), findViewById(R.id.llPage5));
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);

        // EditTexts
        etFirstName = findViewById(R.id.etFirstName); etMiddleName = findViewById(R.id.etMiddleName); etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail); etStudentIdReg = findViewById(R.id.etStudentIdReg); etPasswordReg = findViewById(R.id.etPasswordReg);
        etConfirmPassword = findViewById(R.id.etConfirmPassword); etContactNumber = findViewById(R.id.etContactNumber); etAddress = findViewById(R.id.etAddress);
        etYearLevel = findViewById(R.id.etYearLevel);

        // Spinners
        spCampusBranch = findViewById(R.id.spCampusBranch); spCollege = findViewById(R.id.spCollege); spCourse = findViewById(R.id.spCourse);

        // Error TextViews
        tvFirstNameError = findViewById(R.id.tvFirstNameError); tvLastNameError = findViewById(R.id.tvLastNameError); tvEmailError = findViewById(R.id.tvEmailError);
        tvStudentIdError = findViewById(R.id.tvStudentIdError); tvPasswordError = findViewById(R.id.tvPasswordError); tvConfirmPasswordError = findViewById(R.id.tvConfirmPasswordError);
        tvContactNumberError = findViewById(R.id.tvContactNumberError); tvAddressError = findViewById(R.id.tvAddressError); tvCampusBranchError = findViewById(R.id.tvCampusBranchError);
        tvCollegeError = findViewById(R.id.tvCollegeError); tvCourseError = findViewById(R.id.tvCourseError); tvYearLevelError = findViewById(R.id.tvYearLevelError);
    }

    private void setupSpinners() {
        // Static Spinners
        spCampusBranch.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SchoolDataProvider.getCampusBranches()));
        spCollege.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SchoolDataProvider.getColleges()));
        
        // Dynamic Course Spinner
        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        spCourse.setAdapter(courseAdapter);
    }

    private void setupNavigation() {
        btnNext.setOnClickListener(v -> viewModel.nextPage());
        btnPrevious.setOnClickListener(v -> viewModel.previousPage());
        btnSubmit.setOnClickListener(v -> viewModel.registerUser());
    }

    private void observeViewModel() {
        viewModel.getCurrentPage().observe(this, page -> {
            for (int i = 0; i < pages.size(); i++) { pages.get(i).setVisibility(page == (i + 1) ? View.VISIBLE : View.GONE); }
            btnPrevious.setVisibility(page > 1 ? View.VISIBLE : View.INVISIBLE);
            btnNext.setVisibility(page < pages.size() ? View.VISIBLE : View.GONE);
            btnSubmit.setVisibility(page == pages.size() ? View.VISIBLE : View.GONE);
        });
        viewModel.getRegistrationResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Registration Failed - Check all fields or Student ID may already exist", Toast.LENGTH_LONG).show();
            }
        });
        
        // Observe course list changes and update the adapter directly
        viewModel.availableCourses.observe(this, courses -> {
            courseAdapter.clear();
            courseAdapter.addAll(courses);
            courseAdapter.notifyDataSetChanged();
        });

        // Error Observers
        observeError(viewModel.firstNameError, tvFirstNameError); observeError(viewModel.lastNameError, tvLastNameError);
        observeError(viewModel.emailError, tvEmailError); observeError(viewModel.studentIdError, tvStudentIdError);
        observeError(viewModel.passwordError, tvPasswordError); observeError(viewModel.confirmPasswordError, tvConfirmPasswordError);
        observeError(viewModel.contactNumberError, tvContactNumberError); observeError(viewModel.addressError, tvAddressError);
        observeError(viewModel.campusBranchError, tvCampusBranchError); observeError(viewModel.collegeError, tvCollegeError);
        observeError(viewModel.courseError, tvCourseError); observeError(viewModel.yearLevelError, tvYearLevelError);
    }

    private void bindViewsToViewModel() {
        bindEditText(etFirstName, viewModel.firstName); bindEditText(etMiddleName, viewModel.middleName); bindEditText(etLastName, viewModel.lastName);
        bindEditText(etEmail, viewModel.email); bindEditText(etStudentIdReg, viewModel.studentId); bindEditText(etPasswordReg, viewModel.password);
        bindEditText(etConfirmPassword, viewModel.confirmPassword); bindEditText(etContactNumber, viewModel.contactNumber); bindEditText(etAddress, viewModel.address);
        bindEditText(etYearLevel, viewModel.yearLevel);

        bindSpinner(spCampusBranch, viewModel.campusBranch); 
        bindSpinner(spCollege, viewModel.college);
        bindSpinner(spCourse, viewModel.course);
    }

    private void observeError(LiveData<String> errorLiveData, TextView errorTextView) {
        errorLiveData.observe(this, error -> {
            errorTextView.setText(error);
            errorTextView.setVisibility(error != null ? View.VISIBLE : View.GONE);
        });
    }

    private void bindEditText(final EditText editText, final MutableLiveData<String> liveData) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (!s.toString().equals(liveData.getValue())) {
                    liveData.setValue(s.toString());
                }
            }
        });
    }

    private void bindSpinner(final Spinner spinner, final MutableLiveData<String> liveData) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getItemAtPosition(position).toString().equals(liveData.getValue())) {
                    liveData.setValue((String) parent.getItemAtPosition(position));
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}