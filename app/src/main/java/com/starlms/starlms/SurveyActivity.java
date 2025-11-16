package com.starlms.starlms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.SurveyAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivitySurveysBinding;
import com.starlms.starlms.entity.Survey;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SurveyActivity extends AppCompatActivity implements SurveyAdapter.OnSurveyClickListener {

    private ActivitySurveysBinding binding;
    private SurveyAdapter adapter;

    private final ActivityResultLauncher<Intent> surveyDetailsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadSurveys();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurveysBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarSurveys);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarSurveys.setNavigationOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        loadSurveys();
    }

    private void setupRecyclerView() {
        binding.recyclerViewSurveys.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadSurveys() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

            List<Survey> surveys = db.surveyDao().getSurveysByStatus("Đã đăng");

            runOnUiThread(() -> {
                if (adapter == null) {
                    adapter = new SurveyAdapter(surveys, this);
                    binding.recyclerViewSurveys.setAdapter(adapter);
                } else {
                    adapter.setSurveys(surveys);
                }
            });
        });
    }

    @Override
    public void onSurveyClick(Survey survey) {

        Intent intent = new Intent(this, SurveyDetailsActivity.class);
        intent.putExtra(SurveyDetailsActivity.EXTRA_SURVEY_ID, survey.getSurveyId());
        surveyDetailsLauncher.launch(intent);
    }
}
