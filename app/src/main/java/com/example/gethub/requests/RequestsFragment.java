// File: com.example.gethub.requests.RequestsFragment.java (UPDATED)
package com.example.gethub.requests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gethub.R;
import com.example.gethub.databinding.FragmentRequestsBinding; // New Binding Import
import com.example.gethub.home.HomeActivity;
import com.example.gethub.profile.ProfileActivity;

public class RequestsFragment extends Fragment {

    private FragmentRequestsBinding binding;
    private RequestsViewModel viewModel;
    private RequestsAdapter adapter;
    private String loggedInStudentId = "1234567890"; // Using the mock default ID for testing.

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize View Binding
        binding = FragmentRequestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // FIX 1: Ensure ViewModel is scoped to the Activity for data consistency
        viewModel = new ViewModelProvider(requireActivity()).get(RequestsViewModel.class);
        adapter = new RequestsAdapter(new java.util.ArrayList<>(), requireContext());

        // 2. Setup RecyclerView
        binding.rvRequestsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRequestsList.setAdapter(adapter);

        // 3. Setup Header Actions
        setupHeaderActions();

        // 4. Load Data and Observe
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            loggedInStudentId = homeActivity.loggedInStudentId;
        }

        if (loggedInStudentId != null && !loggedInStudentId.isEmpty()) {
            // FIX 2: Call loadRequests in onResume/onStart for reliable data refresh
            // We will rely on the onStart() lifecycle method for visibility checks.
        }

        observeViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        // FIX 3: Force data refresh every time the fragment becomes visible
        if (loggedInStudentId != null && !loggedInStudentId.isEmpty() && viewModel != null) {
            viewModel.loadRequests(loggedInStudentId);
        }
    }

    private void setupHeaderActions() {
        // Back Button: Returns the user to the Dashboard (Home Fragment)
        binding.ibBack.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                // FIX: Access the now public binding property and programmatically click Home
                homeActivity.binding.bnvMain.setSelectedItemId(R.id.nav_home);
            }
        });

        // Profile Icon Click: Redirects to ProfileActivity (Consistent with Notification Page)
        binding.ibProfile.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                Intent intent = new Intent(requireContext(), ProfileActivity.class);
                // FIX: Access the now public loggedInUser property
                intent.putExtra(ProfileActivity.EXTRA_USER, homeActivity.loggedInUser);
                startActivity(intent);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getRequestList().observe(getViewLifecycleOwner(), requests -> {
            if (requests == null || requests.isEmpty()) {
                binding.tvNoRequests.setVisibility(View.VISIBLE);
                binding.rvRequestsList.setVisibility(View.GONE);
            } else {
                adapter.updateList(requests);
                binding.tvNoRequests.setVisibility(View.GONE);
                binding.rvRequestsList.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}