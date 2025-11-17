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
import com.example.gethub.databinding.FragmentRegistrationPage3Binding;

import java.util.function.Consumer;

/**
 * Registration Page 3: Student ID, Password, and Confirm Password.
 */
public class RegistrationPage3Fragment extends Fragment {

    private FragmentRegistrationPage3Binding binding;
    private RegistrationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrationPage3Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        // --- 1. Setup Input Listeners ---
        setupTextWatcher(binding.etStudentID, viewModel::onStudentIdChanged);
        setupTextWatcher(binding.etPassword, viewModel::onPasswordChanged);
        setupTextWatcher(binding.etConfirmPassword, viewModel::onConfirmPasswordChanged);

        // --- 2. Observe ViewModel State and Errors ---

        // Student ID Error
        viewModel.getErrStudentId().observe(getViewLifecycleOwner(), error -> {
            binding.errStudentId.setText(error);
            binding.errStudentId.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // Password Error
        viewModel.getErrPassword().observe(getViewLifecycleOwner(), error -> {
            binding.errPassword.setText(error);
            binding.errPassword.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // Confirm Password Error
        viewModel.getErrConfirmPassword().observe(getViewLifecycleOwner(), error -> {
            binding.errConfirmPassword.setText(error);
            binding.errConfirmPassword.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // --- 3. Load existing data (for back navigation) ---
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            // Student ID
            if (!binding.etStudentID.getText().toString().equals(user.getStudentId())) {
                binding.etStudentID.setText(user.getStudentId());
            }
            // Password
            if (!binding.etPassword.getText().toString().equals(user.getPassword())) {
                binding.etPassword.setText(user.getPassword());
            }
            // Confirm Password (loaded from the ViewModel's dedicated LiveData)
            String currentConfirmPass = viewModel.getConfirmPassword().getValue();
            if (currentConfirmPass != null && !binding.etConfirmPassword.getText().toString().equals(currentConfirmPass)) {
                binding.etConfirmPassword.setText(currentConfirmPass);
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