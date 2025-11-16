package com.starlms.starlms.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.model.LeaveRequestWithUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminLeaveRequestAdapter extends RecyclerView.Adapter<AdminLeaveRequestAdapter.LeaveRequestViewHolder> {

    private List<LeaveRequestWithUser> requestList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onApproveClick(LeaveRequestWithUser request);
        void onRejectClick(LeaveRequestWithUser request);
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LeaveRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_list_item_leave_request, parent, false);
        return new LeaveRequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveRequestViewHolder holder, int position) {
        LeaveRequestWithUser currentItem = requestList.get(position);

        holder.studentName.setText(currentItem.getUser() != null ? currentItem.getUser().getFullName() : "N/A");
        holder.reason.setText("Lý do: " + currentItem.getLeaveRequest().getReason());
        holder.requestDate.setText(dateFormat.format(new Date(currentItem.getLeaveRequest().getRequestDate())));

        String status = currentItem.getLeaveRequest().getStatus();
        holder.status.setText("Trạng thái: " + status);

        // SỬA Ở ĐÂY: Dùng chuỗi tiếng Việt
        switch (status) {
            case "Đã duyệt":
                holder.status.setTextColor(Color.GREEN);
                holder.approveButton.setVisibility(View.GONE);
                holder.rejectButton.setVisibility(View.GONE);
                break;
            case "Bị từ chối":
                holder.status.setTextColor(Color.RED);
                holder.approveButton.setVisibility(View.GONE);
                holder.rejectButton.setVisibility(View.GONE);
                break;
            default: // "Chưa duyệt"
                holder.status.setTextColor(Color.DKGRAY);
                holder.approveButton.setVisibility(View.VISIBLE);
                holder.rejectButton.setVisibility(View.VISIBLE);
                break;
        }

        holder.approveButton.setOnClickListener(v -> {
            if (listener != null) listener.onApproveClick(currentItem);
        });

        holder.rejectButton.setOnClickListener(v -> {
            if (listener != null) listener.onRejectClick(currentItem);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void setRequests(List<LeaveRequestWithUser> requests) {
        this.requestList = requests;
        notifyDataSetChanged();
    }

    static class LeaveRequestViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, requestDate, reason, status;
        Button approveButton, rejectButton;

        public LeaveRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.tv_student_name);
            requestDate = itemView.findViewById(R.id.tv_request_date);
            reason = itemView.findViewById(R.id.tv_leave_reason);
            status = itemView.findViewById(R.id.tv_leave_status);
            approveButton = itemView.findViewById(R.id.btn_approve);
            rejectButton = itemView.findViewById(R.id.btn_reject);
        }
    }
}
