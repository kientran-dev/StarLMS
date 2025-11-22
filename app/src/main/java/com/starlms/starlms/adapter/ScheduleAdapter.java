package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.entity.Schedule;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<Schedule> scheduleList;
    private final SimpleDateFormat timeFormat;

    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);

        String startTime = timeFormat.format(schedule.getDateTime());

        holder.tvSessionTime.setText(String.format("Thời gian: %s", startTime));
        holder.tvSubject.setText(schedule.getSubject());
        holder.tvTeacher.setText(schedule.getTeacherInfo());
        holder.tvClassroom.setText(schedule.getClassroom());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionTime;
        TextView tvSubject;
        TextView tvTeacher;
        TextView tvClassroom;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSessionTime = itemView.findViewById(R.id.tv_session_time);
            tvSubject = itemView.findViewById(R.id.tv_schedule_subject);
            tvTeacher = itemView.findViewById(R.id.tv_schedule_teacher);
            tvClassroom = itemView.findViewById(R.id.tv_schedule_classroom);
        }
    }
}
