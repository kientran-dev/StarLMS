package com.starlms.starlms;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.ExamAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityExamsBinding;
import com.starlms.starlms.entity.Exam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExamsActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "COURSE_ID";
    public static final String EXTRA_COURSE_NAME = "COURSE_NAME";

    private ActivityExamsBinding binding;
    private ExamAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExamsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        String courseName = getIntent().getStringExtra(EXTRA_COURSE_NAME);
        // Hardcoding user ID for now
        int userId = 1;

        setSupportActionBar(binding.toolbarExams);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(courseName != null ? courseName : "Bảng điểm");
        }
        binding.toolbarExams.setNavigationOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        loadExams(courseId, userId);
    }

    private void setupRecyclerView() {
        binding.recyclerViewExams.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadExams(int courseId, int userId) {
        if (courseId == -1 || userId == -1) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Exam> exams = db.examDao().getExamsForCourse(userId, courseId);

            // If there are no exams, create some dummy data for display
            if (exams.isEmpty()) {
                exams.add(new Exam(0, "Bài kiểm tra giữa kỳ", 8.5, userId, courseId));
                exams.add(new Exam(0, "Bài kiểm tra cuối kỳ", null, userId, courseId));
                // Also insert them into the database for persistence
                for(Exam exam : exams) {
                    db.examDao().insert(exam);
                }
            }

            runOnUiThread(() -> {
                adapter = new ExamAdapter(exams);
                binding.recyclerViewExams.setAdapter(adapter);
            });
        });
    }
}
