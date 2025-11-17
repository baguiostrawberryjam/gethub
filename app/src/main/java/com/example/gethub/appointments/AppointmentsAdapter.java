// File: com.example.gethub.appointments.AppointmentsAdapter.java
package com.example.gethub.appointments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gethub.R;
import com.example.gethub.data.DataRepository;
import com.example.gethub.models.Appointment;
import com.example.gethub.models.RequestTicket;
import com.example.gethub.requests.TicketDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private Context context;

    public AppointmentsAdapter(List<Appointment> appointmentList, Context context) {
        this.appointmentList = appointmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.bind(appointment, context);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public void updateList(List<Appointment> newList) {
        this.appointmentList = newList;
        notifyDataSetChanged();
    }


    // --- View Holder ---
    public static class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAppointmentDate;
        TextView tvAppointmentDocName;
        TextView tvAppointmentStatus;
        Appointment currentAppointment;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppointmentDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvAppointmentDocName = itemView.findViewById(R.id.tvAppointmentDocName);
            tvAppointmentStatus = itemView.findViewById(R.id.tvAppointmentStatus);
            itemView.setOnClickListener(this);
        }

        public void bind(Appointment appointment, Context context) {
            this.currentAppointment = appointment;

            // 1. Date and Time
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
            tvAppointmentDate.setText(sdf.format(new Date(appointment.getScheduledDate())));

            // 2. Linked Document
            tvAppointmentDocName.setText(String.format("Linked to Ticket: %s",
                    appointment.getTicketId()));

            // 3. Status Tag (Business Rule: Color and Text)
            tvAppointmentStatus.setText(appointment.getStatus());
            int colorResId = getStatusColorResId(appointment.getStatus());
            tvAppointmentStatus.getBackground().setTint(ContextCompat.getColor(context, colorResId));

            int colorResBgId = getStatusColorBgResId(appointment.getStatus());
            tvAppointmentStatus.getBackground().setTint(ContextCompat.getColor(context, colorResBgId));
        }

        @Override
        public void onClick(View v) {
            // CRITICAL STEP: Retrieve the linked RequestTicket object using the ticketId for the detail view
            RequestTicket linkedTicket = DataRepository.getTicketById(currentAppointment.getTicketId());

            if (linkedTicket != null) {
                // Navigation to Ticket Detail Activity (Reusing the existing activity)
                Intent intent = new Intent(v.getContext(), TicketDetailActivity.class);
                intent.putExtra(TicketDetailActivity.EXTRA_TICKET, linkedTicket);
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "Error: Linked ticket not found. (ID: " + currentAppointment.getTicketId() + ")", Toast.LENGTH_SHORT).show();
            }
        }

        // Reusing the status color logic for Appointments
        private int getStatusColorResId(String status) {
            switch (status) {
                case "Pending":
                    return R.color.status_approved; // Yellow/Orange
                case "Completed":
                    return R.color.status_completed; // Green
                case "Canceled":
                    return R.color.status_rejected; // Red
                default:
                    return R.color.status_processing; // Gray
            }
        }

        private int getStatusColorBgResId(String status) {
            switch (status) {
                case "Pending":
                    return R.color.status_approved_bg; // Yellow/Orange
                case "Completed":
                    return R.color.status_completed_bg; // Green
                case "Canceled":
                    return R.color.status_rejected_bg; // Red
                default:
                    return R.color.status_processing; // Gray
            }
        }
    }
}