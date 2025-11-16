package com.starlms.starlms;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivitySurveyDetailsBinding;
import com.starlms.starlms.entity.Survey;
import com.starlms.starlms.entity.SurveyResponse;
import com.starlms.starlms.entity.UserSurveyCompletion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SurveyDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_SURVEY_ID = "SURVEY_ID";
    private ActivitySurveyDetailsBinding binding;
    private int surveyId;
    private long currentUserId = 1; // TODO: Replace with actual logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurveyDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        surveyId = getIntent().getIntExtra(EXTRA_SURVEY_ID, -1);
        if (surveyId == -1) {
            finish();
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
            Survey currentSurvey = db.surveyDao().findById(surveyId);
            UserSurveyCompletion completion = db.userSurveyCompletionDao().getCompletion(currentUserId, surveyId);

            runOnUiThread(() -> {
                if (currentSurvey != null) {
                    binding.tvSurveyDetailsTitle.setText(currentSurvey.getTitle());
                    binding.tvSurveyDetailsDescription.setText(currentSurvey.getDescription());
                    if (completion != null) {
                        disableForm();
                    }
                }
            });
        });
    }

    private void submitSurvey() {
        String responseText = binding.etSurveyFeedback.getText().toString().trim();
        if (responseText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập phản hồi của bạn", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            long submissionTime = System.currentTimeMillis();

            // 1. Lưu câu trả lời với thời gian
            SurveyResponse newResponse = new SurveyResponse(0, surveyId, currentUserId, responseText, submissionTime);
            db.surveyResponseDao().insert(newResponse);

            // 2. Đánh dấu là đã hoàn thành
            UserSurveyCompletion newCompletion = new UserSurveyCompletion(currentUserId, surveyId);
            db.userSurveyCompletionDao().insert(newCompletion);

            runOnUiThread(() -> {
                Toast.makeText(this, "Cảm ơn bạn đã gửi khảo sát!", Toast.LENGTH_SHORT).show();
                disableForm();
            });
        });
    }

    private void disableForm() {
        binding.etSurveyFeedback.setEnabled(false);
        binding.btnSubmitSurvey.setEnabled(false);
        binding.btnSubmitSurvey.setText("Đã khảo sát");
    }
}
