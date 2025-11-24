// File: com.example.gethub.home.DashboardFragment.java (UPDATED with Header Actions)
package com.example.gethub.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.widget.TextView; // Make sure this is imported
import android.widget.Toast;

import androidx.core.content.ContextCompat; // For color tinting
import com.example.gethub.data.DataRepository;
import com.example.gethub.models.Notification;
import com.example.gethub.models.RequestTicket;
import com.example.gethub.profile.ImageConverter;
import com.example.gethub.requests.TicketDetailActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

                byte[] profileImage = user.getUserImage();
                if (profileImage != null && profileImage.length > 0) {
                    Bitmap bitmap = ImageConverter.toBitmap(profileImage);
                    binding.ibProfile.setImageBitmap(bitmap);
                } else {
                    binding.ibProfile.setImageResource(R.drawable.bg_prof_circular);
                }
                // binding.tvWelcome.setText(String.format("Welcome, %s!", welcomeName));
            }
        });

        // Observe Request Summary Data (Total, Pending, Completed)
        viewModel.getTotalRequests().observe(getViewLifecycleOwner(), count -> {
            binding.llTotalRequestsCard.setVisibility(View.VISIBLE);
            binding.tvTotalRequests.setText(String.valueOf(count));
        });
        viewModel.getPendingRequests().observe(getViewLifecycleOwner(), count -> {
            binding.tvPendingRequests.setText(String.valueOf(count));
        });
        viewModel.getCompletedRequests().observe(getViewLifecycleOwner(), count -> {
            binding.tvCompletedRequests.setText(String.valueOf(count));
        });

        // Observe Notifications (Toggles placeholder OR binds the latest notification)
        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                // No notifications: Show placeholder, hide item
                binding.tvNoNotifications.setVisibility(View.VISIBLE);
                binding.layoutRecentNotification.getRoot().setVisibility(View.GONE); // Hide the include's root
            } else {
                // Notifications exist: Hide placeholder, show item
                binding.tvNoNotifications.setVisibility(View.GONE);
                binding.layoutRecentNotification.getRoot().setVisibility(View.VISIBLE); // Show the include's root

                Notification recentNotification = notifications.get(0);

                // --- FIX: Access views via the nested binding object ---
                TextView tvMessage = binding.layoutRecentNotification.tvNotificationMessage;
                TextView tvTime = binding.layoutRecentNotification.tvNotificationTime;
                TextView tvStatus = binding.layoutRecentNotification.tvNotificationStatus;
                // --- End Fix ---

                // --- Bind Data (Logic from NotificationAdapter) ---
                tvMessage.setText(recentNotification.getMessage());

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
                tvTime.setText(sdf.format(new Date(recentNotification.getTimestamp())));

                if (recentNotification.isRead()) {
                    tvStatus.setText("Read");
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
                } else {
                    tvStatus.setText("New/View Ticket");
                    // Ensure the text color is set correctly for "New"
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral_deep));
                }

                // --- Make the item clickable (using the include's root) ---
                binding.layoutRecentNotification.getRoot().setOnClickListener(v -> {
                    RequestTicket linkedTicket = DataRepository.getTicketById(recentNotification.getLinkedId());
                    if (linkedTicket != null) {
                        Intent intent = new Intent(requireContext(), TicketDetailActivity.class);
                        intent.putExtra(TicketDetailActivity.EXTRA_TICKET, linkedTicket);
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "Error: Linked ticket not found.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}