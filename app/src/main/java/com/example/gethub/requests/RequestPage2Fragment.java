// File: com.example.gethub.requests.RequestPage2Fragment.java
package com.example.gethub.requests;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gethub.R;
import com.example.gethub.databinding.FragmentRequestPage2Binding;
import com.example.gethub.models.RequestTicket;

public class RequestPage2Fragment extends Fragment {

    private FragmentRequestPage2Binding binding;
    private RequestViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRequestPage2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) { // FIX: Corrected typo
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RequestViewModel.class);

        setupFields();
        setupPurposeSpinner(binding.spPurpose);
        observeViewModel();
    }

    private void setupFields() {
        // Document Type Display
        viewModel.getSelectedDocument().observe(getViewLifecycleOwner(), doc -> {
            if (doc != null) {
                binding.etDocumentTypeDisplay.setText(doc.getDocName());
            }
        });

        // Other Purpose Text Listener
        binding.etOtherPurpose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onOtherPurposeTextChanged(s.toString());
            }
        });
    }

    private void setupPurposeSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(requireContext(), android.R.layout.simple_spinner_item, getResources().getTextArray(R.array.spPurpose_choices)) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // Disable placeholder
            }

            // View when spinner is CLOSED
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
                } else {
                    ((TextView) view).setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral_deep));
                }
                return view;
            }

            // FIX: View when spinner is OPENED (DROPDOWN) - Ensures gray text
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
                } else {
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral_deep));
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
                viewModel.onPurposeSelected(selected);

                // Show/Hide Other Purpose EditText
                if ("Others (Please Specify)".equals(selected)) {
                    binding.etOtherPurpose.setVisibility(View.VISIBLE);
                    binding.etOtherPurpose.requestFocus();
                } else {
                    binding.etOtherPurpose.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void observeViewModel() {
        // Observe Errors
        viewModel.getErrPurpose().observe(getViewLifecycleOwner(), error -> {
            binding.errPurpose.setText(error);
            binding.errPurpose.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}