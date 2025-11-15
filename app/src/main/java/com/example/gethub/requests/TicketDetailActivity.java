// File: com.example.gethub.requests.TicketDetailActivity.java (REFACTORED WITH BINDING)
package com.example.gethub.requests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gethub.R;
import com.example.gethub.databinding.ActivityTicketDetailBinding; // NEW IMPORT
import com.example.gethub.home.HomeActivity;
import com.example.gethub.models.RequestTicket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TICKET = "extra_request_ticket";
    private ActivityTicketDetailBinding binding; // NEW BINDING VARIABLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater()); // INITIALIZE BINDING
        setContentView(binding.getRoot()); // SET CONTENT VIEW VIA BINDING

        RequestTicket ticket = getIntent().getParcelableExtra(EXTRA_TICKET);

        if (ticket == null) {
            Toast.makeText(this, "Ticket data not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- 1. Display Core Details (Using Binding) ---
        binding.tvTicketHeader.setText(String.format("TICKET %s", ticket.getTicketId()));

        // Document Type Display (FIX: Uses binding.tvTicketDocType)
        String documentType = ticket.getDocumentType();
        if (documentType == null || documentType.isEmpty()) {
            binding.tvTicketDocType.setText("N/A (Data Error)");
        } else {
            binding.tvTicketDocType.setText(documentType);
        }

        binding.tvTicketPurpose.setText(ticket.getPurposeOfRequest());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        binding.tvTicketReqDate.setText(sdf.format(new Date(ticket.getRequestDate())));

        // --- 2. Status Display and Coloring (Business Rule Implementation) ---
        binding.tvTicketStatus.setText(ticket.getStatus().toUpperCase());

        int colorResId = getStatusColorResId(ticket.getStatus());
        // Set the background tint color (Fix: uses getBackground().setTint for shape drawable)
        binding.tvTicketStatus.getBackground().setTint(ContextCompat.getColor(this, colorResId));

        // --- 3. Navigation Button (Using Binding) ---
        binding.btnBackToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(TicketDetailActivity.this, HomeActivity.class);

            // FIX: Add flag to force navigation to the Dashboard Fragment
            intent.putExtra(HomeActivity.EXTRA_NAVIGATE_TO_DASHBOARD, true);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // --- 4. Action Buttons Visibility (Using Binding) ---
        boolean isProcessingOrApproved = "Processing".equals(ticket.getStatus()) || "Approved".equals(ticket.getStatus());
        boolean isPickupAndAppointmentExists = "Pick-up".equals(ticket.getDeliveryMethod()) && ticket.getAppointmentId() != null;

        binding.btnCancelRequest.setVisibility(isProcessingOrApproved ? View.VISIBLE : View.GONE);
        binding.btnViewAppointment.setVisibility(isPickupAndAppointmentExists ? View.VISIBLE : View.GONE);
    }

    /**
     * Maps ticket status string to the appropriate Android color resource ID (Business Rule).
     */
    private int getStatusColorResId(String status) {
        switch (status) {
            case "Processing":
                return android.R.color.darker_gray;
            case "Approved":
                return android.R.color.holo_orange_dark;
            case "Completed":
                return android.R.color.holo_green_dark;
            case "Rejected":
                return android.R.color.holo_red_dark;
            default:
                return R.color.black;
        }
    }
}