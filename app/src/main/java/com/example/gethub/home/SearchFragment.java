package com.example.gethub.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gethub.R;

/**
 * A simple {@link Fragment} subclass that displays the search layout.
 */
public class SearchFragment extends Fragment {

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is where you inflate your XML layout file.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // This line is the key part that links this Java class to the
        // R.layout.fragment_search XML file.
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // You can find and interact with views from your layout here,
        // but it's often cleaner to do so in onViewCreated.

        return view;
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state
     * has been restored in to the view. This is a good place to set up listeners
     * or find view references.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Example:
        // TextView myTextView = view.findViewById(R.id.my_text_view);
        // Button myButton = view.findViewById(R.id.my_button);
        // myButton.setOnClickListener(v -> {
        //    // Handle button click
        // });
    }
}