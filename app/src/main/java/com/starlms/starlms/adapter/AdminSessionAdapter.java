package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.entity.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminSessionAdapter extends RecyclerView.Adapter<AdminSessionAdapter.SessionViewHolder> {

    private List<Session> sessionList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private OnItemInteractionListener listener;

    public interface OnItemInteractionListener {
        void onItemLongClick(Session session);
    }

    public void setOnItemInteractionListener(OnItemInteractionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_list_item_session, parent, false);
        return new SessionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session currentSession = sessionList.get(position);

        holder.sessionName.setText(currentSession.getTitle());
        // SỬA Ở ĐÂY: Dùng getSessionDate() và getClassroom()
        holder.location.setText("Địa điểm: " + currentSession.getClassroom());

        if (currentSession.getSessionDate() > 0) {
            holder.dateTime.setText(dateFormat.format(new Date(currentSession.getSessionDate())));
        } else {
            holder.dateTime.setText("Chưa có thời gian");
        }
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public void setSessions(List<Session> sessions) {
        this.sessionList = sessions;
        notifyDataSetChanged();
    }

    class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView sessionName, dateTime, location;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionName = itemView.findViewById(R.id.tv_session_name);
            dateTime = itemView.findViewById(R.id.tv_session_datetime);
            location = itemView.findViewById(R.id.tv_session_location);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(sessionList.get(position));
                    return true;
                }
                return false;
            });
        }
    }
}
