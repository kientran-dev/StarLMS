package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.databinding.ItemSessionBinding;
import com.starlms.starlms.entity.Session;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private final List<Session> sessions;
    private final String courseType;
    private final OnSessionInteractionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnSessionInteractionListener {
        void onCheckInClick(Session session);
        void onRequestLeaveClick(Session session);
        void onVideoClick(Session session);
    }

    public SessionAdapter(List<Session> sessions, String courseType, OnSessionInteractionListener listener) {
        this.sessions = sessions;
        this.courseType = courseType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSessionBinding binding = ItemSessionBinding.inflate(layoutInflater, parent, false);
        return new SessionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        holder.bind(session, courseType, listener, dateFormat);
    }

    @Override
    public int getItemCount() {
        return sessions != null ? sessions.size() : 0;
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        private final ItemSessionBinding binding;

        public SessionViewHolder(ItemSessionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Session session, final String courseType, final OnSessionInteractionListener listener, SimpleDateFormat dateFormat) {
            binding.textViewSessionTitle.setText(session.getTitle());
            binding.textViewSessionDate.setText(dateFormat.format(session.getSessionDate()));

            if ("online".equalsIgnoreCase(courseType)) {
                binding.layoutOfflineActions.setVisibility(View.GONE);
                itemView.setOnClickListener(v -> listener.onVideoClick(session));
            } else { // Offline
                binding.layoutOfflineActions.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(null); // No action on item click for offline courses

                // Check-in time limit (2 hours)
                long twoHoursInMillis = 2 * 60 * 60 * 1000;
                boolean isCheckInEnabled = System.currentTimeMillis() <= session.getSessionDate() + twoHoursInMillis;
                binding.buttonCheckIn.setEnabled(isCheckInEnabled);

                binding.buttonCheckIn.setOnClickListener(v -> listener.onCheckInClick(session));
                binding.buttonRequestLeave.setOnClickListener(v -> listener.onRequestLeaveClick(session));
            }
        }
    }
}
