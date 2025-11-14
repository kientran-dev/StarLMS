package com.starlms.starlms.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.databinding.ItemSurveyBinding;
import com.starlms.starlms.entity.Survey;

import java.util.List;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder> {

    private List<Survey> surveyList;
    private final OnSurveyClickListener listener;

    public interface OnSurveyClickListener {
        void onSurveyClick(Survey survey);
    }

    public SurveyAdapter(List<Survey> surveyList, OnSurveyClickListener listener) {
        this.surveyList = surveyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSurveyBinding binding = ItemSurveyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SurveyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        Survey survey = surveyList.get(position);
        holder.bind(survey, listener);
    }

    @Override
    public int getItemCount() {
        return surveyList.size();
    }

    public void setSurveys(List<Survey> surveys) {
        this.surveyList = surveys;
        notifyDataSetChanged();
    }

    static class SurveyViewHolder extends RecyclerView.ViewHolder {
        private final ItemSurveyBinding binding;

        public SurveyViewHolder(ItemSurveyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Survey survey, final OnSurveyClickListener listener) {
            binding.tvSurveyTitle.setText(survey.getTitle());
            binding.tvSurveyDescription.setText(survey.getDescription());

            if (survey.isCompleted()) {
                binding.tvSurveyStatus.setVisibility(View.VISIBLE);
                // Make the item look disabled or less prominent
                itemView.setAlpha(0.6f);
            } else {
                binding.tvSurveyStatus.setVisibility(View.GONE);
                itemView.setAlpha(1.0f);
            }

            itemView.setOnClickListener(v -> listener.onSurveyClick(survey));
        }
    }
}
