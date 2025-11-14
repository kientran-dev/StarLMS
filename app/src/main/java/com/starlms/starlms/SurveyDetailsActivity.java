package com.starlms.starlms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivitySurveyDetailsBinding;
import com.starlms.starlms.entity.Survey;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SurveyDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_SURVEY_ID = "SURVEY_ID";
    private ActivitySurveyDetailsBinding binding;
    private int surveyId;
    private Survey currentSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurveyDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        surveyId = getIntent().getIntExtra(EXTRA_SURVEY_ID, -1);
        if (surveyId == -1) {
            finish(); // Cannot proceed without a survey ID
            return;
        }

        setSupportActionBar(binding.toolbarSurveyDetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarSurveyDetails.setNavigationOnClickListener(v -> onBackPressed());

        loadSurveyDetails();

        binding.btnSubmitSurvey.setOnClickListener(v -> submitSurvey());
    }

    private void loadSurveyDetails() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            currentSurvey = db.surveyDao().findById(surveyId);

            runOnUiThread(() -> {
                if (currentSurvey != null) {
                    binding.tvSurveyDetailsTitle.setText(currentSurvey.getTitle());
                    binding.tvSurveyDetailsDescription.setText(currentSurvey.getDescription());
                }
            });
        });
    }

    private void submitSurvey() {
        if (currentSurvey == null) return;

        currentSurvey.setCompleted(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            db.surveyDao().updateSurvey(currentSurvey);

            runOnUiThread(() -> {
                Toast.makeText(this, "Khảo sát đã được gửi!", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish(); // Go back to the survey list
            });
        });
    }
}
