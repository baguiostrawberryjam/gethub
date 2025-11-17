// File: com.example.gethub.requests.RequestsAdapter.java
package com.example.gethub.requests;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gethub.R;
import com.example.gethub.models.RequestTicket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    private List<RequestTicket> requestList;
    private Context context;

    public RequestsAdapter(List<RequestTicket> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request_ticket, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RequestTicket ticket = requestList.get(position);
        holder.bind(ticket, context);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void updateList(List<RequestTicket> newList) {
        this.requestList = newList;
        notifyDataSetChanged();
    }


    // --- View Holder ---
    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTicketDocName;
        TextView tvTicketSummary;
        TextView tvTicketStatusTag;
        RequestTicket currentTicket;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicketDocName = itemView.findViewById(R.id.tvTicketDocName);
            tvTicketSummary = itemView.findViewById(R.id.tvTicketSummary);
            tvTicketStatusTag = itemView.findViewById(R.id.tvTicketStatusTag);
            itemView.setOnClickListener(this);
        }

        public void bind(RequestTicket ticket, Context context) {
            this.currentTicket = ticket;

            // 1. Document Name
            tvTicketDocName.setText(ticket.getDocumentType());

            // 2. Summary (ID + Date)
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String summary = String.format("%s • Requested %s",
                    ticket.getTicketId(),
                    sdf.format(new Date(ticket.getRequestDate())));
            tvTicketSummary.setText(summary);

            // 3. Status Tag (Business Rule: Color and Text)
            tvTicketStatusTag.setText(ticket.getStatus().toUpperCase());

            int colorResId = getStatusColorResId(ticket.getStatus());
            tvTicketStatusTag.setTextColor(ContextCompat.getColor(context, colorResId));

            int colorResBgId = getStatusColorBgResId(ticket.getStatus());
            tvTicketStatusTag.getBackground().setTint(ContextCompat.getColor(context, colorResBgId));
        }

        @Override
        public void onClick(View v) {
            // Navigation to Ticket Detail Activity
            Intent intent = new Intent(v.getContext(), TicketDetailActivity.class);
            intent.putExtra(TicketDetailActivity.EXTRA_TICKET, currentTicket);
            v.getContext().startActivity(intent);
        }

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
}