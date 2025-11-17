// File: com.example.gethub.appointments.ScheduleActivity.java (SYNCHRONIZED)
package com.example.gethub.appointments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.gethub.R;
import com.example.gethub.data.DataRepository; // Required for fetching ticket in success flow
import com.example.gethub.databinding.ActivityScheduleBinding;
import com.example.gethub.models.RequestTicket; // Required for passing ticket in success flow
import com.example.gethub.requests.TicketDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    public static final String EXTRA_TICKET_ID = "extra_ticket_id_for_schedule";
    private ActivityScheduleBinding binding;
    private AppointmentsViewModel viewModel; // NEW
    private String linkedTicketId;
    private long selectedDateTimestamp = 0; // Timestamp for selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );


        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AppointmentsViewModel.class); // NEW

        initialize();

        // Observe scheduling status from ViewModel
        observeViewModel(); // NEW
    }

    private void initialize() {
        linkedTicketId = getIntent().getStringExtra(EXTRA_TICKET_ID);
        if (linkedTicketId == null) {
            Toast.makeText(this, "Error: Ticket ID missing for scheduling.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // NOTE: The activity needs to load existing appointment data here if 'EXTRA_APPOINTMENT_ID' was passed
        // This is necessary to show the current schedule if it's a reschedule operation. (Future enhancement)

        binding.tvLinkedTicketId.setText(String.format("Ticket ID: %s", linkedTicketId));

        setupHeader();
        setupTimeSlotSpinner();
        setupDateSelection();
        setupSubmission();
    }

    // NEW: Observe ViewModel for scheduling results
    private void observeViewModel() {
        viewModel.getScheduleStatus().observe(this, status -> {
            if (status != null && !status.isEmpty()) {
                if (status.startsWith("SUCCESS")) {
                    // Show a customized success dialog based on the ViewModel's result
                    showSuccessDialog(status.contains("rescheduled"));
                } else if (status.startsWith("FAILED")) {
                    // Show a customized failure message (e.g., reschedule limit reached)
                    Toast.makeText(this, status.replace("FAILED: ", ""), Toast.LENGTH_LONG).show();
                } else if (status.startsWith("ERROR")) {
                    Toast.makeText(this, "System Error: " + status, Toast.LENGTH_LONG).show();
                }

                // --- MVVM FIX: Call the ViewModel method to reset the state ---
                viewModel.clearScheduleStatus();
            }
        });
    }

    private void setupHeader() {
        binding.ibBack.setOnClickListener(v -> finish());
    }

    private void setupTimeSlotSpinner() {
        // ... (No change to spinner setup)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.time_slots,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spTimeSlot.setAdapter(adapter);

        binding.spTimeSlot.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateSubmissionButtonState();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                updateSubmissionButtonState();
            }
        });
    }

    private void setupDateSelection() {
        // ... (No change to date selection setup)
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog dpDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Normalize selected date to midnight
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year1, monthOfYear, dayOfMonth);
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    selectedCalendar.set(Calendar.MINUTE, 0);
                    selectedCalendar.set(Calendar.SECOND, 0);
                    selectedCalendar.set(Calendar.MILLISECOND, 0);

                    // --- PAST DATE RESTRICTION ---
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);

                    if (selectedCalendar.getTimeInMillis() < today.getTimeInMillis()) {
                        binding.errDate.setText("Cannot select a date in the past.");
                        binding.errDate.setVisibility(View.VISIBLE);
                        binding.tvSelectedDate.setText(getString(R.string.hint_tap_to_select_date));
                        selectedDateTimestamp = 0;
                    } else {
                        selectedDateTimestamp = selectedCalendar.getTimeInMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        binding.tvSelectedDate.setText(sdf.format(selectedCalendar.getTime()));
                        binding.errDate.setVisibility(View.GONE);
                    }
                    updateSubmissionButtonState();
                }, year, month, day);

        dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        binding.tvSelectedDate.setOnClickListener(v -> dpDialog.show());
    }

    // REFACTORED: Now calls ViewModel instead of simulating success
    private void setupSubmission() {
        binding.btnConfirmSchedule.setOnClickListener(v -> {
            if (selectedDateTimestamp == 0 || binding.spTimeSlot.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select a date and time slot.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Combine Date and Time Slot into final timestamp
            long finalScheduledTimestamp = getFinalScheduledTimestamp();

            // 2. Call ViewModel to execute the business logic (initial schedule or reschedule)
            viewModel.scheduleOrRescheduleAppointment(linkedTicketId, finalScheduledTimestamp);
        });

        updateSubmissionButtonState();
    }

    private void updateSubmissionButtonState() {
        boolean isValid = selectedDateTimestamp != 0 && binding.spTimeSlot.getSelectedItemPosition() > 0;
        binding.btnConfirmSchedule.setEnabled(isValid);
    }

    // NEW HELPER: Calculates the final timestamp
    private long getFinalScheduledTimestamp() {
        String timeSlot = binding.spTimeSlot.getSelectedItem().toString();
        // Time slots are typically: "Select Time Slot", "8:00 AM", "9:00 AM", etc.

        // Get the normalized selected date
        Calendar finalCalendar = Calendar.getInstance();
        finalCalendar.setTimeInMillis(selectedDateTimestamp);

        // Simple parsing logic: Assumes timeSlot is in HH:mm AM/PM format (e.g., "8:00 AM")
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date timePart = timeFormat.parse(timeSlot);

            if (timePart != null) {
                Calendar timeCalendar = Calendar.getInstance();
                timeCalendar.setTime(timePart);

                // Set the time of day on the selected date
                finalCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                finalCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            }
        } catch (Exception e) {
            // Fallback if time parsing fails (e.g., if user selects the placeholder)
            Toast.makeText(this, "Time slot parsing error.", Toast.LENGTH_SHORT).show();
            return selectedDateTimestamp;
        }

        return finalCalendar.getTimeInMillis();
    }

    // REFACTORED: Now fetches data to fix the navigation bug (passes the Parcelable ticket)
    private void showSuccessDialog(boolean isReschedule) {
        String title = isReschedule ? "Reschedule Confirmed" : "Appointment Confirmed";
        String message = isReschedule ?
                "Your pickup appointment has been successfully rescheduled!" :
                "Your pickup appointment has been successfully scheduled! You may view the updated status on your ticket.";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("View Ticket", (dialog, which) -> {
                    // FIX: Fetch the updated ticket and pass it to TicketDetailActivity
                    RequestTicket ticket = DataRepository.getTicketById(linkedTicketId);

                    Intent intent = new Intent(this, TicketDetailActivity.class);
                    if (ticket != null) {
                        // Pass the entire Parcelable object (Fixes the "Ticket data not found" bug)
                        intent.putExtra(TicketDetailActivity.EXTRA_TICKET, ticket);
                    }

                    startActivity(intent);
                    finish();
                })
                .show();
    }
}