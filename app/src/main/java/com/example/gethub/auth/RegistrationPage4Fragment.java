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

import com.example.gethub.databinding.FragmentRegistrationPage4Binding;

import java.util.function.Consumer;

/**
 * Registration Page 4: Contact Number and Address.
 */
public class RegistrationPage4Fragment extends Fragment {

    private FragmentRegistrationPage4Binding binding;
    private RegistrationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationPage4Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        // --- 1. Setup Input Listeners (Contact and Address Fields) ---
        setupTextWatcher(binding.etContactNumber, viewModel::onContactNumberChanged);
        setupTextWatcher(binding.etAddressNo, viewModel::onAddressNoChanged);
        setupTextWatcher(binding.etAddressStreet, viewModel::onAddressStreetChanged);
        setupTextWatcher(binding.etAddressBarangay, viewModel::onAddressBarangayChanged);
        setupTextWatcher(binding.etAddressCity, viewModel::onAddressCityChanged);
        setupTextWatcher(binding.etAddressProvince, viewModel::onAddressProvinceChanged);

        // --- 2. Observe ViewModel State and Errors ---
        observeErrors();

        // --- 3. Load existing data (for back navigation) ---
        loadExistingData();
    }

    private void observeErrors() {
        // Contact Number Error
        viewModel.getErrContactNumber().observe(getViewLifecycleOwner(), error -> {
            binding.errContactNumber.setText(error);
            binding.errContactNumber.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // Address Errors
        viewModel.getErrAddressNo().observe(getViewLifecycleOwner(), error -> {
            binding.errAddressNo.setText(error);
            binding.errAddressNo.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });
        viewModel.getErrAddressStreet().observe(getViewLifecycleOwner(), error -> {
            binding.errAddressStreet.setText(error);
            binding.errAddressStreet.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });
        viewModel.getErrAddressBarangay().observe(getViewLifecycleOwner(), error -> {
            binding.errAddressBarangay.setText(error);
            binding.errAddressBarangay.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });
        viewModel.getErrAddressCity().observe(getViewLifecycleOwner(), error -> {
            binding.errAddressCity.setText(error);
            binding.errAddressCity.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });
        viewModel.getErrAddressProvince().observe(getViewLifecycleOwner(), error -> {
            binding.errAddressProvince.setText(error);
            binding.errAddressProvince.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void loadExistingData() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            // Contact Number
            if (!binding.etContactNumber.getText().toString().equals(user.getContactNumber())) {
                binding.etContactNumber.setText(user.getContactNumber());
            }
            // Address Fields
            if (!binding.etAddressNo.getText().toString().equals(user.getAddressNo())) {
                binding.etAddressNo.setText(user.getAddressNo());
            }
            if (!binding.etAddressStreet.getText().toString().equals(user.getAddressStreet())) {
                binding.etAddressStreet.setText(user.getAddressStreet());
            }
            if (!binding.etAddressBarangay.getText().toString().equals(user.getAddressBarangay())) {
                binding.etAddressBarangay.setText(user.getAddressBarangay());
            }
            if (!binding.etAddressCity.getText().toString().equals(user.getAddressCity())) {
                binding.etAddressCity.setText(user.getAddressCity());
            }
            if (!binding.etAddressProvince.getText().toString().equals(user.getAddressProvince())) {
                binding.etAddressProvince.setText(user.getAddressProvince());
            }
        });
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
        // Call validation initially to load errors if data already exists
        consumer.accept(editText.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}