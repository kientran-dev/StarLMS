package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.databinding.ItemAssignmentBinding;
import com.starlms.starlms.entity.Assignment;

import java.util.List;
import java.util.Locale;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private final List<Assignment> assignments;
    private final OnAssignmentInteractionListener listener;

    public interface OnAssignmentInteractionListener {
        void onStartTest(int testId);
    }

    public AssignmentAdapter(List<Assignment> assignments, OnAssignmentInteractionListener listener) {
        this.assignments = assignments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemAssignmentBinding binding = ItemAssignmentBinding.inflate(inflater, parent, false);
        return new AssignmentViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return assignments != null ? assignments.size() : 0;
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        private final ItemAssignmentBinding binding;
        private final OnAssignmentInteractionListener listener;

        public AssignmentViewHolder(ItemAssignmentBinding binding, OnAssignmentInteractionListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void bind(final Assignment assignment) {
            binding.assignmentTitle.setText(assignment.getTest().getTestName());
            binding.assignmentDescription.setText(assignment.getTest().getDescription());

            if (assignment.getGrade() != null) {
                // If the assignment has been graded
                binding.actionContainer.setVisibility(View.VISIBLE);
                binding.scoreContainer.setVisibility(View.VISIBLE);
                binding.startButton.setVisibility(View.GONE);
                // Format score to show decimal only if needed
                double score = assignment.getGrade().getScore();
                if (score == (long) score) {
                    binding.scoreValue.setText(String.format(Locale.US, "%d", (long) score));
                } else {
                    binding.scoreValue.setText(String.format(Locale.US, "%.1f", score));
                }

            } else {
                // If the assignment has not been taken yet
                binding.actionContainer.setVisibility(View.VISIBLE);
                binding.startButton.setVisibility(View.VISIBLE);
                binding.scoreContainer.setVisibility(View.GONE);
                binding.startButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onStartTest(assignment.getTest().getTestId());
                    }
                });
            }
        }
    }
}
