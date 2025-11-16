package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.entity.Survey;

import java.util.ArrayList;
import java.util.List;

public class AdminSurveyListAdapter extends RecyclerView.Adapter<AdminSurveyListAdapter.SurveyViewHolder> {

    private List<Survey> surveyList = new ArrayList<>();
    private OnItemInteractionListener listener;
    private final boolean isPublishedTab;

    public AdminSurveyListAdapter(boolean isPublishedTab) {
        this.isPublishedTab = isPublishedTab;
    }

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_list_item_survey, parent, false);
        return new SurveyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        Survey currentSurvey = surveyList.get(position);
        holder.textViewTitle.setText(currentSurvey.getTitle());
        holder.textViewDescription.setText(currentSurvey.getDescription());

        if (isPublishedTab) {
            holder.publishButton.setVisibility(View.GONE);
        } else {
            holder.publishButton.setVisibility(View.VISIBLE);
            holder.publishButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPublishClick(currentSurvey);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return surveyList.size();
    }

    public void setSurveys(List<Survey> surveys) {
        this.surveyList = surveys;
        notifyDataSetChanged();
    }

    class SurveyViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final Button publishButton;

        public SurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.tv_survey_title);
            textViewDescription = itemView.findViewById(R.id.tv_survey_description);
            publishButton = itemView.findViewById(R.id.btn_publish_survey);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(surveyList.get(position));
                    return true;
                }
                return false;
            });
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(surveyList.get(position));
                }
            });
        }
    }

    public interface OnItemInteractionListener {
        void onItemLongClick(Survey survey);
        void onPublishClick(Survey survey);
        void onItemClick(Survey survey);
    }

    public void setOnItemInteractionListener(OnItemInteractionListener listener) {
        this.listener = listener;
    }
}
