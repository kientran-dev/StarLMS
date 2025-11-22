package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.databinding.ItemExamBinding;
import com.starlms.starlms.entity.Exam;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {

    private final List<Exam> exams;

    public ExamAdapter(List<Exam> exams) {
        this.exams = exams;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemExamBinding binding = ItemExamBinding.inflate(layoutInflater, parent, false);
        return new ExamViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = exams.get(position);
        holder.bind(exam);
    }

    @Override
    public int getItemCount() {
        return exams != null ? exams.size() : 0;
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {
        private final ItemExamBinding binding;

        public ExamViewHolder(ItemExamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Exam exam) {
            binding.textViewExamName.setText(exam.getExamName());
            if (exam.getScore() != null) {
                binding.textViewExamScore.setText(String.valueOf(exam.getScore()));
            } else {
                binding.textViewExamScore.setText(""); // Empty if no score
            }
        }
    }
}
