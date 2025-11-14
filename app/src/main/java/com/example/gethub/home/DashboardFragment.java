// File: com.example.gethub.home.DashboardFragment.java
package com.example.gethub.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.gethub.R;

public class DashboardFragment extends Fragment {

    private HomeViewModel viewModel;

    // View IDs from fragment_dashboard.xml (Adhering to Naming Standards)
    private TextView tvWelcome;
    private TextView tvTotalRequests;
    private TextView tvPendingRequests;
    private TextView tvCompletedRequests;
    private TextView tvNoNotifications;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment (R.layout.fragment_dashboard)
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialize ViewModel (MUST be scoped to the hosting Activity)
        // This ensures the fragment uses the exact same ViewModel instance loaded by HomeActivity
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // 2. Initialize Views (Finding views using your IDs)
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvTotalRequests = view.findViewById(R.id.tvTotalRequests);
        // Note: The layout has 'tvCompletedRequests' but is missing an explicit 'Approved' counter
        tvPendingRequests = view.findViewById(R.id.tvPendingRequests);
        tvCompletedRequests = view.findViewById(R.id.tvCompletedRequests);
        tvNoNotifications = view.findViewById(R.id.tvNoNotifications);

        // 3. Observe LiveData
        observeViewModel();
    }

    private void observeViewModel() {
        // Observe User Data (for Welcome message)
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Safely get first name for welcome message
                String welcomeName = user.getFirstName();
                if (welcomeName == null || welcomeName.isEmpty()) {
                    welcomeName = user.getStudentId();
                }
                tvWelcome.setText(String.format("Welcome, %s!", welcomeName));
            }
        });

        // Observe Request Summary Data
        viewModel.getTotalRequests().observe(getViewLifecycleOwner(), count -> {
            tvTotalRequests.setText(String.valueOf(count));
        });

        // Note: We use the 'Pending' card for Pending status
        viewModel.getPendingRequests().observe(getViewLifecycleOwner(), count -> {
            tvPendingRequests.setText(String.valueOf(count));
        });

        // Note: We use the 'Completed' card for Completed status
        viewModel.getCompletedRequests().observe(getViewLifecycleOwner(), count -> {
            tvCompletedRequests.setText(String.valueOf(count));
        });

        // Observe Notifications (Toggles the placeholder text visibility)
        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                tvNoNotifications.setVisibility(View.VISIBLE);
            } else {
                tvNoNotifications.setVisibility(View.GONE);
                // If a RecyclerView were here, you would update the adapter.
            }
        });
    }
}