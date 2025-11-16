package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.model.CourseWithTeacher;

import java.util.ArrayList;
import java.util.List;

public class AdminCourseAdapter extends RecyclerView.Adapter<AdminCourseAdapter.CourseViewHolder> {

    private List<CourseWithTeacher> courseList = new ArrayList<>();
    private OnItemInteractionListener listener;

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_list_item_course, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseWithTeacher currentItem = courseList.get(position);
        holder.textViewName.setText(currentItem.getCourse().getName());
        holder.textViewType.setText(currentItem.getCourse().getType());
        if (currentItem.getTeacher() != null) {
            holder.textViewTeacher.setText("Phụ trách: " + currentItem.getTeacher().getName());
        } else {
            holder.textViewTeacher.setText("Chưa có giảng viên");
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void setCourses(List<CourseWithTeacher> courses) {
        this.courseList = courses;
        notifyDataSetChanged();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewType;
        private final TextView textViewTeacher;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tv_course_name);
            textViewType = itemView.findViewById(R.id.tv_course_type);
            textViewTeacher = itemView.findViewById(R.id.tv_course_teacher);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(courseList.get(position));
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnItemInteractionListener {
        void onItemLongClick(CourseWithTeacher courseWithTeacher);
    }

    public void setOnItemInteractionListener(OnItemInteractionListener listener) {
        this.listener = listener;
    }
}
