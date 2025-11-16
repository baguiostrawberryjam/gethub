// File: com.example.gethub.requests.TicketDetailActivity.java (REFINEMENT 2: APPOINTMENT DETAILS)
package com.example.gethub.requests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gethub.R;
import com.example.gethub.appointments.ScheduleActivity;
import com.example.gethub.data.DataRepository;
import com.example.gethub.databinding.ActivityTicketDetailBinding;
import com.example.gethub.home.HomeActivity;
import com.example.gethub.models.RequestTicket;
import com.example.gethub.models.Appointment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TICKET = "extra_request_ticket";
    private ActivityTicketDetailBinding binding;
    private RequestTicket currentTicket; // Keep the ticket handy for the new onResume logic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load the initial ticket and save it to the instance variable
        currentTicket = getIntent().getParcelableExtra(EXTRA_TICKET);

        if (currentTicket == null) {
            Toast.makeText(this, "Ticket data not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        displayTicketDetails(currentTicket);
        setupNavigationAndActions(currentTicket);
    }

    // NEW: Reload data on resume to refresh appointment status after returning from ScheduleActivity
    @Override
    protected void onResume() {
        super.onResume();
        // Since the currentTicket only holds the old data, fetch the latest version
        if (currentTicket != null && currentTicket.getTicketId() != null) {
            RequestTicket latestTicket = DataRepository.getTicketById(currentTicket.getTicketId());
            if (latestTicket != null) {
                currentTicket = latestTicket;
                displayTicketDetails(currentTicket);
                setupNavigationAndActions(currentTicket);
            }
        }
    }

    private void displayTicketDetails(RequestTicket ticket) {
        // --- 1. Display Core Details ---
        binding.tvTicketHeader.setText(String.format("TICKET %s", ticket.getTicketId()));
        binding.tvTicketDocType.setText(ticket.getDocumentType() != null ? ticket.getDocumentType() : "N/A (Data Error)");
        binding.tvTicketPurpose.setText(ticket.getPurposeOfRequest());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
        binding.tvTicketReqDate.setText(sdf.format(new Date(ticket.getRequestDate())));

        // --- 2. Status Display and Coloring ---
        binding.tvTicketStatus.setText(ticket.getStatus().toUpperCase());

        int colorResId = getStatusColorResId(ticket.getStatus());
        binding.tvTicketStatus.setTextColor(ContextCompat.getColor(this, colorResId));

        int colorResBgId = getStatusColorBgResId(ticket.getStatus());
        binding.tvTicketStatus.getBackground().setTint(ContextCompat.getColor(this, colorResBgId));

        // --- 3. Handle Appointment Date Display (NEW LOGIC) ---
        handleAppointmentDetails(ticket);
    }

    private void setupNavigationAndActions(RequestTicket ticket) {
        // --- Navigation Button ---
        binding.btnBackToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(TicketDetailActivity.this, HomeActivity.class);
            intent.putExtra(HomeActivity.EXTRA_NAVIGATE_TO_DASHBOARD, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Action buttons logic is now handled in handleAppointmentDetails for dynamic state
    }

    /**
     * Handles the display logic for the Appointment Date field and the action button.
     * Business Rules: Reschedule limit, button visibility, button text.
     */
    private void handleAppointmentDetails(RequestTicket ticket) {
        // Only show appointment fields for Pick-up delivery
        boolean isPickUp = "Pick-up".equals(ticket.getDeliveryMethod());
        binding.tvApptDateLabel.setVisibility(isPickUp ? View.VISIBLE : View.GONE);
        binding.tvTicketApptDate.setVisibility(isPickUp ? View.VISIBLE : View.GONE);

        // Assume default state (no appointment action required)
        binding.btnViewAppointment.setVisibility(View.GONE);

        if (!isPickUp) {
            binding.tvTicketApptDate.setText("N/A - Digital Delivery");
            return;
        }

        // --- FIX 1: Declare linkedAppointment as final/effectively final for use in the lambda ---
        final Appointment linkedAppointment;
        if (ticket.getAppointmentId() != null) {
            linkedAppointment = DataRepository.getAppointmentById(ticket.getAppointmentId());
        } else {
            linkedAppointment = null;
        }
        // --- END FIX 1 ---

        boolean canSchedule = "Approved".equals(ticket.getStatus());

        // Default text for "no appointment yet"
        String appointmentDateText = "Not yet scheduled";

        if (linkedAppointment != null) {
            // Appointment exists: display date and set button text to RESCHEDULE
            SimpleDateFormat sdfFull = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
            appointmentDateText = sdfFull.format(new Date(linkedAppointment.getScheduledDate()));

            // Set button text to Reschedule
            binding.btnViewAppointment.setText("RESCHEDULE APPOINTMENT");
            binding.btnViewAppointment.setVisibility(View.VISIBLE);

            // --- Business Rule: Reschedule Limit Check (UI Disable) ---
            if (linkedAppointment.getRescheduleCount() >= 1) {
                // Disable the button if the limit is reached
                binding.btnViewAppointment.setEnabled(false);
                binding.btnViewAppointment.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.neutral_lavender)); // Grey tint
            } else if (canSchedule) {
                // Enable button for rescheduling
                binding.btnViewAppointment.setEnabled(true);
                binding.btnViewAppointment.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_purple)); // Purple tint
            }
        } else if (canSchedule) {
            // No appointment exists but the ticket is in a state that requires one (Approved/Processing)
            // Set button text to SET SCHEDULE
            binding.btnViewAppointment.setText("SET APPOINTMENT SCHEDULE");
            binding.btnViewAppointment.setVisibility(View.VISIBLE);
            binding.btnViewAppointment.setEnabled(true);
            binding.btnViewAppointment.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_purple));
        }

        binding.tvTicketApptDate.setText(appointmentDateText);

        // --- Button Click Listener (FIX 2: Re-adding robust validation) ---
        binding.btnViewAppointment.setOnClickListener(v -> {

            // Re-check reschedule limit for robustness, now using the final/effectively final linkedAppointment
            if (linkedAppointment != null && linkedAppointment.getRescheduleCount() >= 1) {
                Toast.makeText(this, "Reschedule limit reached. You can only reschedule once.", Toast.LENGTH_LONG).show();
                return;
            }

            // PROCEED TO SCHEDULE/RESCHEDULE
            Intent intent = new Intent(TicketDetailActivity.this, ScheduleActivity.class);
            intent.putExtra(ScheduleActivity.EXTRA_TICKET_ID, ticket.getTicketId());

            startActivity(intent);
        });
    }

    /**
     * Maps ticket status string to the appropriate Android color resource ID (Business Rule).
     */


    private int getStatusColorResId(String status) {
        switch (status) {
            case "Processing":
                return R.color.status_processing; // Gray
            case "Approved":
                return R.color.status_approved; // Yellow/Orange
            case "Completed":
                return R.color.status_completed; // Green
            case "Rejected":
                return R.color.status_rejected; // Red
            default:
                return R.color.gray;
        }
    }

    private int getStatusColorBgResId(String status) {
        switch (status) {
            case "Processing":
                return R.color.status_processing_bg; // Gray
            case "Approved":
                return R.color.status_approved_bg; // Yellow/Orange
            case "Completed":
                return R.color.status_completed_bg; // Green
            case "Rejected":
                return R.color.status_rejected_bg; // Red
            default:
                return R.color.gray;
        }
    }
}