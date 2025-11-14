package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.entity.Assignment;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<Assignment> assignments;
    private OnAssignmentInteractionListener listener;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private Button startButton;
        private TextView scoreText;
        private OnAssignmentInteractionListener listener;

        public AssignmentViewHolder(@NonNull View itemView, OnAssignmentInteractionListener listener) {
            super(itemView);
            this.listener = listener;
            title = itemView.findViewById(R.id.assignment_title);
            description = itemView.findViewById(R.id.assignment_description);
            startButton = itemView.findViewById(R.id.start_button);
            scoreText = itemView.findViewById(R.id.score_text);
        }

        public void bind(Assignment assignment) {
            title.setText(assignment.getTest().getTestName());
            description.setText(assignment.getTest().getDescription());

            if (assignment.getGrade() != null) {
                startButton.setVisibility(View.GONE);
                scoreText.setVisibility(View.VISIBLE);
                scoreText.setText("Điểm: " + assignment.getGrade().getScore() + "/" + assignment.getTest().getMaxScore());
            } else {
                startButton.setVisibility(View.VISIBLE);
                scoreText.setVisibility(View.GONE);
                startButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onStartTest(assignment.getTest().getTestId());
                    }
                });
            }
        }
    }
}
