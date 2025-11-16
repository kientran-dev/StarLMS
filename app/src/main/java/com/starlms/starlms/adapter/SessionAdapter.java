package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.entity.Attendance;
import com.starlms.starlms.entity.Session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private final List<Session> sessions;
    private final Map<Integer, Attendance> attendanceMap;
    private final String courseType;
    private final OnSessionInteractionListener listener;

    public interface OnSessionInteractionListener {
        void onCheckInClick(Session session);
        void onRequestLeaveClick(Session session);
        void onVideoClick(Session session);
    }

    public SessionAdapter(List<Session> sessions, Map<Integer, Attendance> attendanceMap, String courseType, OnSessionInteractionListener listener) {
        this.sessions = sessions;
        this.attendanceMap = attendanceMap;
        this.courseType = courseType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        holder.bind(session, attendanceMap.get(session.getSessionId()), courseType, listener);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView dateTextView;
        private final LinearLayout offlineActionsLayout;
        private final Button checkInButton;
        private final Button requestLeaveButton;
        private final Button statusButton;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_session_title);
            dateTextView = itemView.findViewById(R.id.text_view_session_date);
            offlineActionsLayout = itemView.findViewById(R.id.layout_offline_actions);
            checkInButton = itemView.findViewById(R.id.button_check_in);
            requestLeaveButton = itemView.findViewById(R.id.button_request_leave);
            statusButton = itemView.findViewById(R.id.button_status);
        }

        void bind(final Session session, final Attendance attendance, final String courseType, final OnSessionInteractionListener listener) {
            titleTextView.setText(session.getTitle());

            if ("online".equalsIgnoreCase(courseType)) {
                dateTextView.setVisibility(View.GONE);
                offlineActionsLayout.setVisibility(View.GONE);
                statusButton.setVisibility(View.GONE);
                itemView.setOnClickListener(v -> listener.onVideoClick(session));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                dateTextView.setText(sdf.format(new Date(session.getSessionDate())));
                dateTextView.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(null);

                if (attendance != null) {
                    // Status exists, show the status button
                    offlineActionsLayout.setVisibility(View.GONE);
                    statusButton.setVisibility(View.VISIBLE);

                    String status = attendance.getStatus();
                    if ("PRESENT".equalsIgnoreCase(status)) {
                        statusButton.setText("Đã điểm danh");
                    } else if ("LEAVE_REQUESTED".equalsIgnoreCase(status)) {
                        statusButton.setText("Đã xin nghỉ");
                    } else {
                        statusButton.setText(status); // Fallback
                    }
                } else {
                    // No status, show action buttons
                    offlineActionsLayout.setVisibility(View.VISIBLE);
                    statusButton.setVisibility(View.GONE);
                    checkInButton.setOnClickListener(v -> listener.onCheckInClick(session));
                    requestLeaveButton.setOnClickListener(v -> listener.onRequestLeaveClick(session));
                }
            }
        }
    }
}
