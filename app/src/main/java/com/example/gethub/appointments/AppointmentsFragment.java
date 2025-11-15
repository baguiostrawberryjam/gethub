// File: com.example.gethub.appointments.AppointmentsFragment.java (SYNCHRONIZED FIX)
package com.example.gethub.appointments;

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
import com.example.gethub.databinding.FragmentAppointmentsBinding;
import com.example.gethub.home.HomeActivity;
import com.example.gethub.profile.ProfileActivity;

public class AppointmentsFragment extends Fragment {

    private FragmentAppointmentsBinding binding;
    private AppointmentsViewModel viewModel;
    private AppointmentsAdapter adapter;
    private String loggedInStudentId; // Will be set in onViewCreated

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAppointmentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialize ViewModel and Adapter
        viewModel = new ViewModelProvider(requireActivity()).get(AppointmentsViewModel.class);
        adapter = new AppointmentsAdapter(new java.util.ArrayList<>(), requireContext());

        // 2. Setup RecyclerView
        binding.rvAppointmentsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAppointmentsList.setAdapter(adapter);

        // 3. Setup Header Actions
        setupHeaderActions();

        // 4. Retrieve Student ID (Must happen before onStart)
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            loggedInStudentId = homeActivity.loggedInStudentId;
        }

        // 5. Observe LiveData (Called once)
        observeViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        // FIX: Force data refresh every time the fragment becomes visible (tab switch/return)
        if (loggedInStudentId != null && !loggedInStudentId.isEmpty() && viewModel != null) {
            viewModel.loadAppointments(loggedInStudentId);
        }
    }

    private void setupHeaderActions() {
        // Back Button: Programmatically selects the Home tab
        binding.ibBack.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).binding.bnvMain.setSelectedItemId(R.id.nav_home);
            }
        });

        // Profile Icon Click
        binding.ibProfile.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                Intent intent = new Intent(requireContext(), ProfileActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USER, homeActivity.loggedInUser);
                startActivity(intent);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getAppointmentList().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments == null || appointments.isEmpty()) {
                binding.tvNoAppointments.setVisibility(View.VISIBLE);
                binding.rvAppointmentsList.setVisibility(View.GONE);
            } else {
                adapter.updateList(appointments);
                binding.tvNoAppointments.setVisibility(View.GONE);
                binding.rvAppointmentsList.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}