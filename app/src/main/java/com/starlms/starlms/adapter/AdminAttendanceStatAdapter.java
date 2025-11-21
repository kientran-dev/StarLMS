package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.model.AttendanceStat;

import java.util.ArrayList;
import java.util.List;

public class AdminAttendanceStatAdapter extends RecyclerView.Adapter<AdminAttendanceStatAdapter.StatViewHolder> {

    private List<AttendanceStat> stats = new ArrayList<>();

    public void setStats(List<AttendanceStat> stats) {
        this.stats = stats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_list_item_attendance_stat, parent, false);
        return new StatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
        AttendanceStat currentStat = stats.get(position);
        if (currentStat.getUser() != null) {
            holder.studentName.setText(currentStat.getUser().getFullName());
        }
        holder.presentCount.setText("Có mặt: " + currentStat.getPresentCount());
        holder.leaveCount.setText("Xin nghỉ: " + currentStat.getLeaveCount());
        holder.absentCount.setText("Vắng: " + currentStat.getAbsentCount());
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    static class StatViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, presentCount, leaveCount, absentCount;

        public StatViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.tv_student_name);
            presentCount = itemView.findViewById(R.id.tv_present_count);
            leaveCount = itemView.findViewById(R.id.tv_leave_count);
            absentCount = itemView.findViewById(R.id.tv_absent_count);
        }
    }
}
