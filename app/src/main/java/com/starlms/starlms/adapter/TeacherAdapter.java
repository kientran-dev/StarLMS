package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.databinding.ItemTeacherBinding;
import com.starlms.starlms.model.TeacherWithCourse;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private final List<TeacherWithCourse> teachersWithCourses;
    private final OnTeacherClickListener listener;

    public interface OnTeacherClickListener {
        void onTeacherClick(TeacherWithCourse teacherWithCourse);
    }

    public TeacherAdapter(List<TeacherWithCourse> teachersWithCourses, OnTeacherClickListener listener) {
        this.teachersWithCourses = teachersWithCourses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTeacherBinding binding = ItemTeacherBinding.inflate(inflater, parent, false);
        return new TeacherViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherWithCourse teacherWithCourse = teachersWithCourses.get(position);
        holder.bind(teacherWithCourse, listener);
    }

    @Override
    public int getItemCount() {
        return teachersWithCourses != null ? teachersWithCourses.size() : 0;
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {
        private final ItemTeacherBinding binding;

        public TeacherViewHolder(ItemTeacherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final TeacherWithCourse teacherWithCourse, final OnTeacherClickListener listener) {
            binding.teacherName.setText(teacherWithCourse.getTeacher().getName());
            binding.courseName.setText(teacherWithCourse.getCourse().getName());
            itemView.setOnClickListener(v -> listener.onTeacherClick(teacherWithCourse));
        }
    }
}
