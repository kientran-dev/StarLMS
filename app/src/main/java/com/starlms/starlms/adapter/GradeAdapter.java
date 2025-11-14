package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.databinding.ItemGradeBinding;
import com.starlms.starlms.entity.GradeInfo;

import java.util.List;
import java.util.Locale;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private final List<GradeInfo> gradeInfos;

    public GradeAdapter(List<GradeInfo> gradeInfos) {
        this.gradeInfos = gradeInfos;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemGradeBinding binding = ItemGradeBinding.inflate(layoutInflater, parent, false);
        return new GradeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        GradeInfo gradeInfo = gradeInfos.get(position);
        holder.bind(gradeInfo);
    }

    @Override
    public int getItemCount() {
        return gradeInfos != null ? gradeInfos.size() : 0;
    }

    static class GradeViewHolder extends RecyclerView.ViewHolder {
        private final ItemGradeBinding binding;

        public GradeViewHolder(ItemGradeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(GradeInfo gradeInfo) {
            binding.textViewTestName.setText(gradeInfo.getTestName());
            if (gradeInfo.getScore() >= 0) {
                String scoreText = String.format(Locale.getDefault(), "%.1f / %d", gradeInfo.getScore(), gradeInfo.getMaxScore());
                binding.textViewScore.setText(scoreText);
            } else {
                binding.textViewScore.setText(""); // Empty if no score
            }
        }
    }
}
