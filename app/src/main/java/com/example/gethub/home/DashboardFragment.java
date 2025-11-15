// File: com.example.gethub.home.DashboardFragment.java (UPDATED with Header Actions)
package com.example.gethub.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gethub.R;
import com.example.gethub.databinding.FragmentDashboardBinding;
import com.example.gethub.notifications.NotificationActivity;
import com.example.gethub.profile.ProfileActivity;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private HomeViewModel viewModel;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialize ViewModel (Scoped to the hosting Activity)
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // 2. Setup Header Actions (MOVED HERE)
        setupHeaderActions();

        // 3. Observe LiveData
        observeViewModel();
    }

    /**
     * Handles clicks for the Notification and Profile icons, passing data from HomeActivity.
     */
    private void setupHeaderActions() {
        if (!(getActivity() instanceof HomeActivity)) return;
        HomeActivity homeActivity = (HomeActivity) getActivity();

        // Notification Button Click (Using binding.ibNotifications)
        binding.ibNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(homeActivity, NotificationActivity.class);
            // Access the public loggedInStudentId from the Activity
            intent.putExtra(NotificationActivity.EXTRA_USER_ID, homeActivity.loggedInStudentId);
            startActivity(intent);
        });

        // Profile Icon Click (Using binding.ibProfile)
        binding.ibProfile.setOnClickListener(v -> {
            Intent intent = new Intent(homeActivity, ProfileActivity.class);
            // Access the public loggedInUser object from the Activity
            intent.putExtra(ProfileActivity.EXTRA_USER, homeActivity.loggedInUser);
            startActivity(intent);
        });
    }


    private void observeViewModel() {
        // Observe User Data (for Welcome message)
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                String welcomeName = user.getFirstName();
                if (welcomeName == null || welcomeName.isEmpty()) {
                    welcomeName = user.getStudentId();
                }
                binding.tvWelcome.setText(String.format("Welcome, %s!", welcomeName));
            }
        });

        // Observe Request Summary Data
        viewModel.getTotalRequests().observe(getViewLifecycleOwner(), count -> {
            binding.llTotalRequestsCard.setVisibility(View.VISIBLE); // Ensure card is visible
            binding.tvTotalRequests.setText(String.valueOf(count));
        });

        viewModel.getPendingRequests().observe(getViewLifecycleOwner(), count -> {
            binding.tvPendingRequests.setText(String.valueOf(count));
        });

        viewModel.getCompletedRequests().observe(getViewLifecycleOwner(), count -> {
            binding.tvCompletedRequests.setText(String.valueOf(count));
        });

        // Observe Notifications (Toggles the placeholder text visibility)
        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                binding.tvNoNotifications.setVisibility(View.VISIBLE);
            } else {
                binding.tvNoNotifications.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}