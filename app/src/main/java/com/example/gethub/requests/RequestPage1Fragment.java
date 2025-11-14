// File: com.example.gethub.requests.RequestPage1Fragment.java (UPDATED)
package com.example.gethub.requests;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gethub.R;
import com.example.gethub.databinding.FragmentRequestPage1Binding;

import java.util.ArrayList;

public class RequestPage1Fragment extends Fragment {

    private FragmentRequestPage1Binding binding;
    private RequestViewModel viewModel;
    private ArrayAdapter<String> documentAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRequestPage1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RequestViewModel.class);

        // FIX: Ensure errors are hidden initially (though the observeViewModel call handles this when error == "")
        binding.errDeliveryMethod.setVisibility(View.GONE);
        binding.errDocumentType.setVisibility(View.GONE);

        setupDeliveryButtons();
        setupDocumentSpinner(binding.spDocumentType);
        observeViewModel();
    }

    private void setupDeliveryButtons() {
        // --- Digital Button ---
        binding.btnDigital.setOnClickListener(v -> {
            viewModel.onDeliveryMethodSelected("Digital");
            updateDeliveryButtonUI("Digital");
        });

        // --- Pick-up Button ---
        binding.btnPickup.setOnClickListener(v -> {
            viewModel.onDeliveryMethodSelected("Pick-up");
            updateDeliveryButtonUI("Pick-up");
        });
    }

    private void updateDeliveryButtonUI(String selectedMethod) {
        Button digitalButton = binding.btnDigital;
        Button pickupButton = binding.btnPickup;

        int colorNeutral = ContextCompat.getColor(requireContext(), R.color.neutral_deep);
        int colorWhite = ContextCompat.getColor(requireContext(), R.color.white);

        if ("Digital".equals(selectedMethod)) {
            digitalButton.setBackgroundResource(R.drawable.sel_button_primary);
            digitalButton.setTextColor(colorWhite);
            pickupButton.setBackgroundResource(R.drawable.sh_rounded_input);
            pickupButton.setTextColor(colorNeutral);
        } else if ("Pick-up".equals(selectedMethod)) {
            pickupButton.setBackgroundResource(R.drawable.sel_button_primary);
            pickupButton.setTextColor(colorWhite);
            digitalButton.setBackgroundResource(R.drawable.sh_rounded_input);
            digitalButton.setTextColor(colorNeutral);
        }
    }

    private void setupDocumentSpinner(Spinner spinner) {
        final Context context = requireContext();
        documentAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, new ArrayList<>()) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // Disable "Select Document Type" placeholder
            }

            // View when spinner is CLOSED
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                } else {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.neutral_deep));
                }
                return view;
            }

            // FIX: View when spinner is OPENED (DROPDOWN) - This is what ensures graying out in the list
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.neutral_deep));
                }
                return view;
            }
        };
        documentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(documentAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                // When an item is selected (after interaction), the ViewModel runs validation
                viewModel.onDocumentTypeSelected(selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void observeViewModel() {
        // Observe available document list (conditional based on delivery method)
        viewModel.getAvailableDocumentNames().observe(getViewLifecycleOwner(), names -> {
            documentAdapter.clear();
            documentAdapter.addAll(names);
            documentAdapter.notifyDataSetChanged();
            binding.spDocumentType.setSelection(0);
        });

        // Observe current fee and update the TextView display
        viewModel.getCurrentFee().observe(getViewLifecycleOwner(), fee -> {
            binding.tvServiceFee.setText(String.format("₱%.2f", fee));
        });

        // Observe validation errors (Visibility is tied to error.isEmpty())
        viewModel.getErrDeliveryMethod().observe(getViewLifecycleOwner(), error -> {
            binding.errDeliveryMethod.setText(error);
            binding.errDeliveryMethod.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.getErrDocumentType().observe(getViewLifecycleOwner(), error -> {
            binding.errDocumentType.setText(error);
            binding.errDocumentType.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}