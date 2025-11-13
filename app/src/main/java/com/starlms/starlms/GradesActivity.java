package com.starlms.starlms;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.GradeAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityGradesBinding;
import com.starlms.starlms.entity.Grade;
import com.starlms.starlms.entity.Test;
import com.starlms.starlms.model.GradeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GradesActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "EXTRA_COURSE_ID";
    public static final String EXTRA_COURSE_NAME = "EXTRA_COURSE_NAME";

    private ActivityGradesBinding binding;
    private GradeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGradesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        String courseName = getIntent().getStringExtra(EXTRA_COURSE_NAME);
        // Hardcoding user ID for now
        int userId = 1;

        setSupportActionBar(binding.toolbarGrades);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(courseName != null ? courseName : "Kết quả học tập");
        binding.toolbarGrades.setNavigationOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        loadGrades(courseId, userId);
    }

    private void setupRecyclerView() {
        binding.recyclerViewGrades.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadGrades(int courseId, int userId) {
        if (courseId == -1 || userId == -1) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Test> tests = db.testDao().getTestsForCourse(courseId);
            List<GradeInfo> gradeInfos = new ArrayList<>();

            for (Test test : tests) {
                Grade grade = db.gradeDao().getGradeForTest(userId, test.getTestId());
                double score = (grade != null) ? grade.getScore() : -1; // Use -1 to indicate no score
                gradeInfos.add(new GradeInfo(test.getTestName(), score, test.getMaxScore()));
            }

            runOnUiThread(() -> {
                adapter = new GradeAdapter(gradeInfos);
                binding.recyclerViewGrades.setAdapter(adapter);
            });
        });
    }
}
