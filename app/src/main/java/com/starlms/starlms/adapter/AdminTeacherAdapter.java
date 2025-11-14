package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.entity.Teacher;

import java.util.ArrayList;
import java.util.List;

public class AdminTeacherAdapter extends RecyclerView.Adapter<AdminTeacherAdapter.TeacherViewHolder> {

    private List<Teacher> teacherList = new ArrayList<>();
    private OnItemInteractionListener listener;

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_list_item_teacher, parent, false);
        return new TeacherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher currentTeacher = teacherList.get(position);
        holder.textViewName.setText(currentTeacher.getName());
        holder.textViewEmail.setText(currentTeacher.getEmail());
        holder.textViewPhone.setText(currentTeacher.getPhoneNumber()); // THÊM DÒNG NÀY
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teacherList = teachers;
        notifyDataSetChanged();
    }

    class TeacherViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewEmail;
        private final TextView textViewPhone; // THÊM DÒNG NÀY

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tv_teacher_name);
            textViewEmail = itemView.findViewById(R.id.tv_teacher_email);
            textViewPhone = itemView.findViewById(R.id.tv_teacher_phone); // THÊM DÒNG NÀY

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(teacherList.get(position));
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnItemInteractionListener {
        void onItemLongClick(Teacher teacher);
    }

    public void setOnItemInteractionListener(OnItemInteractionListener listener) {
        this.listener = listener;
    }
}
