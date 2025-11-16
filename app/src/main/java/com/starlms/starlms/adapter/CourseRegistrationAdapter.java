package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.model.CourseWithTeacher;

import java.util.List;
import java.util.Set;

public class CourseRegistrationAdapter extends RecyclerView.Adapter<CourseRegistrationAdapter.RegistrationViewHolder> {

    private List<CourseWithTeacher> allCourses;
    private Set<Integer> registeredCourseIds;
    private OnRegisterClickListener listener;

    public CourseRegistrationAdapter(List<CourseWithTeacher> allCourses, Set<Integer> registeredCourseIds, OnRegisterClickListener listener) {
        this.allCourses = allCourses;
        this.registeredCourseIds = registeredCourseIds;
        this.listener = listener;
    }

    public interface OnRegisterClickListener {
        void onRegisterClick(CourseWithTeacher course);
    }

    @NonNull
    @Override
    public RegistrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_registration, parent, false);
        return new RegistrationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationViewHolder holder, int position) {
        CourseWithTeacher currentItem = allCourses.get(position);
        holder.courseName.setText(currentItem.getCourse().getName());

        if (currentItem.getTeacher() != null) {
            holder.teacherName.setText("GV: " + currentItem.getTeacher().getName());
        } else {
            holder.teacherName.setText("Chưa có giảng viên");
        }

        if (registeredCourseIds.contains(currentItem.getCourse().getCourseId())) {
            holder.registerButton.setText("Đã đăng ký");
            holder.registerButton.setEnabled(false);
        } else {
            holder.registerButton.setText("Đăng ký");
            holder.registerButton.setEnabled(true);
            holder.registerButton.setOnClickListener(v -> listener.onRegisterClick(currentItem));
        }
    }

    @Override
    public int getItemCount() {
        return allCourses.size();
    }

    public void updateRegistrationStatus(int courseId) {
        registeredCourseIds.add(courseId);
        notifyDataSetChanged();
    }

    static class RegistrationViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, teacherName;
        Button registerButton;

        public RegistrationViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.tv_course_name);
            teacherName = itemView.findViewById(R.id.tv_teacher_name);
            registerButton = itemView.findViewById(R.id.btn_register_course);
        }
    }
}
