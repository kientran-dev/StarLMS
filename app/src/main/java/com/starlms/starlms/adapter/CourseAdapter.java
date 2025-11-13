package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.databinding.ItemCourseBinding;
import com.starlms.starlms.entity.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final List<Course> courses;
    private final OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public CourseAdapter(List<Course> courses, OnCourseClickListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCourseBinding binding = ItemCourseBinding.inflate(layoutInflater, parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course, listener);
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ItemCourseBinding binding;

        public CourseViewHolder(ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Course course, final OnCourseClickListener listener) {
            binding.textViewCourseName.setText(course.getName());
            binding.textViewCourseType.setText(course.getType());
            itemView.setOnClickListener(v -> listener.onCourseClick(course));
        }
    }
}
