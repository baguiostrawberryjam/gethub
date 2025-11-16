// File: com.example.gethub.requests.RequestPage1Fragment.java (UPDATED)
package com.example.gethub.requests;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gethub.R;
import com.example.gethub.databinding.FragmentRequestPage1Binding;
import com.example.gethub.home.HomeActivity;

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

        setupCancelRequestButton();
        setupDeliveryButtons();
        setupDocumentSpinner(binding.spDocumentType);
        observeViewModel();
    }
    private void setupCancelRequestButton() {
        // Assuming the ID for the back button in the header is 'ibBack'
        binding.btnBackToDashboard.setOnClickListener(v -> {
            // 1. Create Intent to navigate to HomeActivity
            Intent intent = new Intent(requireActivity(), HomeActivity.class);

            // 2. Add flag to force navigation to the Dashboard Fragment (Home)
            intent.putExtra(HomeActivity.EXTRA_NAVIGATE_TO_DASHBOARD, true);

            // 3. Set flags to clear the RequestActivity stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
            // Finish the current hosting RequestActivity
            requireActivity().finish();
        });
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

        final int TOP_DRAWABLE_INDEX = 1;

        if ("Digital".equals(selectedMethod)) {
            digitalButton.setBackgroundResource(R.drawable.sh_rounded_button_colored);
            digitalButton.setTextColor(colorWhite);
            tintButtonDrawable(digitalButton, colorWhite, TOP_DRAWABLE_INDEX); // TINT WHITE

            pickupButton.setBackgroundResource(R.drawable.sh_rounded_button);
            pickupButton.setTextColor(colorNeutral);
            tintButtonDrawable(pickupButton, colorNeutral, TOP_DRAWABLE_INDEX); // TINT NEUTRAL
        } else if ("Pick-up".equals(selectedMethod)) {
            pickupButton.setBackgroundResource(R.drawable.sh_rounded_button_colored);
            pickupButton.setTextColor(colorWhite);
            tintButtonDrawable(pickupButton, colorWhite, TOP_DRAWABLE_INDEX); // TINT WHITE

            digitalButton.setBackgroundResource(R.drawable.sh_rounded_button);
            digitalButton.setTextColor(colorNeutral);
            tintButtonDrawable(digitalButton, colorNeutral, TOP_DRAWABLE_INDEX); // TINT NEUTRAL
        }
    }

    private void tintButtonDrawable(Button button, int color, int index) {
        // Get all four drawables associated with the button (left, top, right, bottom)
        Drawable[] drawables = button.getCompoundDrawables();
        Drawable drawableToTint = drawables[index];

        if (drawableToTint != null) {
            // Wrap the drawable to allow tinting without affecting the original resource
            Drawable wrappedDrawable = DrawableCompat.wrap(drawableToTint);
            wrappedDrawable = wrappedDrawable.mutate(); // Ensure we are modifying a unique instance
            DrawableCompat.setTint(wrappedDrawable, color);

            // Reapply the drawables back to the button
            button.setCompoundDrawablesWithIntrinsicBounds(
                    index == 0 ? wrappedDrawable : drawables[0], // Left
                    index == 1 ? wrappedDrawable : drawables[1], // Top
                    index == 2 ? wrappedDrawable : drawables[2], // Right
                    index == 3 ? wrappedDrawable : drawables[3]  // Bottom
            );
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

    // Observe validation errors (MODIFIED LOGIC)
        viewModel.getErrDeliveryMethod().observe(getViewLifecycleOwner(), error -> {
            binding.errDeliveryMethod.setText(error);
            // Error is only visible if the error string is NOT empty
            binding.errDeliveryMethod.setVisibility(error.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // FIX: Apply the same logic here. The error text only appears when set by the ViewModel
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