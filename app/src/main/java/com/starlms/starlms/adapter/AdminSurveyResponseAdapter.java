package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.model.SurveyResponseWithUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminSurveyResponseAdapter extends RecyclerView.Adapter<AdminSurveyResponseAdapter.ResponseViewHolder> {

    private List<SurveyResponseWithUser> responseList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @NonNull
    @Override
    public ResponseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_list_item_survey_response, parent, false);
        return new ResponseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseViewHolder holder, int position) {
        SurveyResponseWithUser currentResponse = responseList.get(position);

        if (currentResponse.getUser() != null) {
            holder.textViewUserName.setText(currentResponse.getUser().getFullName());
        } else {
            holder.textViewUserName.setText("Người dùng ẩn danh");
        }

        if (currentResponse.getSurveyResponse() != null) {
            holder.textViewResponseText.setText(currentResponse.getSurveyResponse().getResponseText());
            // Format and display the submission date
            Date submissionDate = new Date(currentResponse.getSurveyResponse().getSubmissionDate());
            holder.textViewSubmissionDate.setText(dateFormat.format(submissionDate));
        }
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }

    public void setResponses(List<SurveyResponseWithUser> responses) {
        this.responseList = responses;
        notifyDataSetChanged();
    }

    static class ResponseViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewUserName;
        private final TextView textViewResponseText;
        private final TextView textViewSubmissionDate;

        public ResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.tv_user_name);
            textViewResponseText = itemView.findViewById(R.id.tv_response_text);
            textViewSubmissionDate = itemView.findViewById(R.id.tv_submission_date);
        }
    }
}
