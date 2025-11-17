package com.example.gethub.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gethub.R;
import com.example.gethub.databinding.FragmentRegistrationPage1Binding;

import java.util.function.Consumer;

/**
 * Registration Page 1: First Name, Middle Name, Last Name.
 */
public class RegistrationPage1Fragment extends Fragment {

    private FragmentRegistrationPage1Binding binding;
    private RegistrationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Use ViewBinding for the fragment's layout
        binding = FragmentRegistrationPage1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the shared ViewModel from the parent Activity
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        // Set up real-time input listeners and pass data to ViewModel
        setupTextWatcher(binding.etFirstName, viewModel::onFirstNameChanged);
        setupTextWatcher(binding.etMiddleName, viewModel::onMiddleNameChanged);
        setupTextWatcher(binding.etLastName, viewModel::onLastNameChanged);

        // Observe validation errors and update error TextViews in real-time
        viewModel.getErrFirstName().observe(getViewLifecycleOwner(), error -> {
            binding.errFirstName.setText(error);
            binding.errFirstName.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.getErrLastName().observe(getViewLifecycleOwner(), error -> {
            binding.errLastName.setText(error);
            binding.errLastName.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // Load existing data back into fields (important when returning from Page 2)
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            // Check if the current value is different before setting to avoid infinite loop
            if (!binding.etFirstName.getText().toString().equals(user.getFirstName())) {
                binding.etFirstName.setText(user.getFirstName());
            }
            if (!binding.etMiddleName.getText().toString().equals(user.getMiddleName())) {
                binding.etMiddleName.setText(user.getMiddleName());
            }
            if (!binding.etLastName.getText().toString().equals(user.getLastName())) {
                binding.etLastName.setText(user.getLastName());
            }
        });
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
    /**
     * Helper method to reduce boilerplate for TextWatchers used for real-time validation.
     * @param editText The EditText view to watch.
     * @param consumer The ViewModel function to call with the new text.
     */
    private void setupTextWatcher(EditText editText, Consumer<String> consumer) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                consumer.accept(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up binding reference
    }
}