package com.example.gethub.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gethub.R;
import com.example.gethub.databinding.FragmentRegistrationPage5Binding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        loadCoursesIntoViewModel();

        setupCampusBranchSpinner(binding.spCampusBranch);
        setupCollegeSpinner(binding.spCollege);
        setupCourseProgramSpinner(binding.spCourseProgram);

        observeErrors();
        observeConditionalCourses();
        setupBackButton();
    }

    private void setupBackButton() {
        // FIX: Use the fragment's View Binding to find the local btnBack
        binding.btnBack.setOnClickListener(v -> {

            // Replicate the logic from RegistrationActivity's OnBackPressedDispatcher
            ViewPager2 viewPager = getViewPager();
            if (viewPager == null) return;

            if (viewPager.getCurrentItem() == 0) {
                // If on the first page, pressing back closes the Activity
                requireActivity().finish();
            } else {
                // On any other page, use the ViewModel to go back
                viewModel.goToPreviousPage(viewPager.getCurrentItem());
            }
        });
    }

    private ViewPager2 getViewPager() {
        if (getActivity() instanceof RegistrationActivity) {
            // Access the public getViewPager() method we created in the Activity
            return ((RegistrationActivity) getActivity()).getViewPager();
        }
        return null;
    }

    private void loadCoursesIntoViewModel() {
        String[] colleges = getResources().getStringArray(R.array.spCollege_choices);
        for (String college : colleges) {
            if (college.equals(getString(R.string.college_placeholder))) continue;
            int resId = getResources().getIdentifier("spCourse_" + getCollegeAbbreviation(college), "array", requireActivity().getPackageName());
            if (resId != 0) {
                List<String> courses = new ArrayList<>();
                courses.add(getString(R.string.course_placeholder)); // Add placeholder first
                courses.addAll(Arrays.asList(getResources().getStringArray(resId)));
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
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(requireContext(), android.R.layout.simple_spinner_item, getResources().getTextArray(R.array.spCampusBranch_choices)) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }
        };
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

        // Pre-selection logic...
    }

    private void setupCollegeSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(requireContext(), android.R.layout.simple_spinner_item, getResources().getTextArray(R.array.spCollege_choices)) {
             @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }
        };
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

        // Pre-selection logic...
    }

    private void setupCourseProgramSpinner(Spinner spinner) {
        List<String> initialList = new ArrayList<>(Arrays.asList(getString(R.string.course_placeholder)));
        courseAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, initialList) {
             @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }
        };
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(courseAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                viewModel.onCourseProgramSelected(position == 0 ? "" : selected);
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
            // Pre-selection logic...
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
