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

import com.example.gethub.databinding.FragmentRegistrationPage2Binding;
import com.example.gethub.models.User;

import java.util.function.Consumer;

/**
 * Registration Page 2: Email and OTP.
 */
public class RegistrationPage2Fragment extends Fragment {

    private FragmentRegistrationPage2Binding binding;
    private RegistrationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrationPage2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        // --- 1. Setup Input Listeners ---
        setupTextWatcher(binding.etEmail, viewModel::onEmailChanged);
        setupTextWatcher(binding.etOTP, viewModel::onOTPChanged);

        // --- 2. Setup Button Click Listener ---
        binding.btnSendCode.setOnClickListener(v -> {
            // This calls the ViewModel function to validate the email, generate OTP, and trigger the AlertDialog in the Activity
            viewModel.generateAndSendOtp();
        });

        // --- 3. Observe ViewModel State and Errors ---

        // Observe email errors
        viewModel.getErrEmail().observe(getViewLifecycleOwner(), error -> {
            binding.errEmail.setText(error);
            // Only show error if it's NOT the verified message ("OTP Verified!")
            binding.errEmail.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);

            // Important UX: Disable email field once OTP is successfully sent to prevent accidental changes
            // Only enable it if isOtpSent is false
            User currentUser = viewModel.getCurrentUser().getValue();
            if (currentUser != null) {
                boolean isOtpSent = Boolean.TRUE.equals(viewModel.getIsOtpSent().getValue());
                binding.etEmail.setEnabled(!isOtpSent);
            }
        });

        // Observe OTP errors/status
        viewModel.getErrOTP().observe(getViewLifecycleOwner(), error -> {
            binding.errOTP.setText(error);
            binding.errOTP.setVisibility(error.isEmpty() || error.equals("OTP Verified!") ? View.GONE : View.VISIBLE);
        });

        // Observe if OTP was sent to enable/disable OTP input field
        viewModel.getIsOtpSent().observe(getViewLifecycleOwner(), isSent -> {
            binding.etOTP.setEnabled(isSent);
            // If OTP is sent, change the "Send Code" button text to "Resend Code"
            binding.btnSendCode.setText(isSent ? "Resend Code" : "Send Code");
        });

        // --- 4. Load existing data (for back navigation) ---
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            // Load Email
            if (!binding.etEmail.getText().toString().equals(user.getEmail())) {
                binding.etEmail.setText(user.getEmail());
            }
            // Load Entered OTP (from ViewModel's dedicated LiveData)
            String currentEnteredOTP = viewModel.getEnteredOTP().getValue();
            if (currentEnteredOTP != null && !binding.etOTP.getText().toString().equals(currentEnteredOTP)) {
                binding.etOTP.setText(currentEnteredOTP);
            }
        });

        // Ensure OTP field is initially disabled until an email is entered and 'Send Code' is pressed.
        binding.etOTP.setEnabled(false);
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