package com.example.gethub.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gethub.R;
import com.example.gethub.databinding.FragmentRegistrationPage5Binding;

import java.util.Arrays;
import java.util.List;

/**
 * Registration Page 5: Campus, College, and Course/Program.
 * Handles dynamic content loading for the Course Spinner based on College selection.
 */
public class RegistrationPage5Fragment extends Fragment {

    private FragmentRegistrationPage5Binding binding;
    private RegistrationViewModel viewModel;
    private ArrayAdapter<String> courseAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationPage5Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        // Load course data from resources and pass to ViewModel
        loadCoursesIntoViewModel();

        // --- 1. Setup Spinner Adapters and Listeners ---
        setupCampusBranchSpinner(binding.spCampusBranch);
        setupCollegeSpinner(binding.spCollege);
        setupCourseProgramSpinner(binding.spCourseProgram);

        // --- 2. Observe ViewModel State and Errors ---
        observeErrors();
        observeConditionalCourses();
    }

    private void loadCoursesIntoViewModel() {
        String[] colleges = getResources().getStringArray(R.array.spCollege_choices);
        for (String college : colleges) {
            int resId = getResources().getIdentifier("spCourse_" + getCollegeAbbreviation(college), "array", requireActivity().getPackageName());
            if (resId != 0) {
                List<String> courses = Arrays.asList(getResources().getStringArray(resId));
                viewModel.setCoursesForCollege(college, courses);
            }
        }
    }

    private String getCollegeAbbreviation(String college) {
        if (college.contains("(")) {
            return college.substring(college.indexOf("(") + 1, college.indexOf(")"));
        }
        return college;
    }

    private void setupCampusBranchSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.spCampusBranch_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                viewModel.onCampusBranchSelected(position == 0 ? "" : selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        if (viewModel.getCurrentUser().getValue() != null && !viewModel.getCurrentUser().getValue().getCampusBranch().isEmpty()) {
            String campus = viewModel.getCurrentUser().getValue().getCampusBranch();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (campus.equals(adapter.getItem(i))) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setupCollegeSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.spCollege_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                viewModel.onCollegeSelected(position == 0 ? "" : selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        if (viewModel.getCurrentUser().getValue() != null && !viewModel.getCurrentUser().getValue().getCollege().isEmpty()) {
            String college = viewModel.getCurrentUser().getValue().getCollege();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (college.equals(adapter.getItem(i))) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setupCourseProgramSpinner(Spinner spinner) {
        courseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, viewModel.getAvailableCourses().getValue());
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(courseAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                boolean isPlaceholder = selected.equals("Select Course/Program");
                viewModel.onCourseProgramSelected(isPlaceholder ? "" : selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void observeConditionalCourses() {
        viewModel.getAvailableCourses().observe(getViewLifecycleOwner(), courses -> {
            courseAdapter.clear();
            courseAdapter.addAll(courses);
            courseAdapter.notifyDataSetChanged();

            if (viewModel.getCurrentUser().getValue() != null && !viewModel.getCurrentUser().getValue().getCourseProgram().isEmpty()) {
                String storedCourse = viewModel.getCurrentUser().getValue().getCourseProgram();
                for (int i = 0; i < courses.size(); i++) {
                    if (storedCourse.equals(courses.get(i))) {
                        binding.spCourseProgram.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    private void observeErrors() {
        viewModel.getErrCampusBranch().observe(getViewLifecycleOwner(), error -> {
            binding.errCampusBranch.setText(error);
            binding.errCampusBranch.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.getErrCollege().observe(getViewLifecycleOwner(), error -> {
            binding.errCollege.setText(error);
            binding.errCollege.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.getErrCourseProgram().observe(getViewLifecycleOwner(), error -> {
            binding.errCourseProgram.setText(error);
            binding.errCourseProgram.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}