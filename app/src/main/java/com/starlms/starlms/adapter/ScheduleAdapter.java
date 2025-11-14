package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.entity.Schedule;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<Schedule> scheduleList;

    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
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
        holder.tvDatetime.setText(schedule.getDatetime());
        holder.tvSubject.setText(schedule.getSubject());
        holder.tvTeacher.setText(schedule.getTeacher());
        holder.tvClassroom.setText(schedule.getClassroom());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvDatetime;
        TextView tvSubject;
        TextView tvTeacher;
        TextView tvClassroom;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDatetime = itemView.findViewById(R.id.tv_schedule_datetime);
            tvSubject = itemView.findViewById(R.id.tv_schedule_subject);
            tvTeacher = itemView.findViewById(R.id.tv_schedule_teacher);
            tvClassroom = itemView.findViewById(R.id.tv_schedule_classroom);
        }
    }
}
