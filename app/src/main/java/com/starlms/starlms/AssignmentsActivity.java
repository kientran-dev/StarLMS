package com.starlms.starlms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.starlms.starlms.adapter.AssignmentAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityAssignmentsBinding;
import com.starlms.starlms.entity.Assignment;
import com.starlms.starlms.entity.Grade;
import com.starlms.starlms.entity.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssignmentsActivity extends AppCompatActivity implements AssignmentAdapter.OnAssignmentInteractionListener {

    private ActivityAssignmentsBinding binding;
    private int courseId;

    private final ActivityResultLauncher<Intent> quizLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Quiz was finished, reload assignments to update the score
                    loadAssignments();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssignmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        courseId = getIntent().getIntExtra("COURSE_ID", -1);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadAssignments();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadAssignments() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Test> tests = db.testDao().getTestsForCourse(courseId);
            List<Assignment> assignments = new ArrayList<>();
            int userId = db.userDao().getAll().get(0).getId(); // Assuming single user

            for (Test test : tests) {
                Grade grade = db.gradeDao().getGradeForTest(userId, test.getTestId());
                assignments.add(new Assignment(test, grade));
            }

            runOnUiThread(() -> {
                AssignmentAdapter adapter = new AssignmentAdapter(assignments, this);
                binding.assignmentsRecyclerView.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onStartTest(int testId) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(QuizActivity.EXTRA_TEST_ID, testId);
        quizLauncher.launch(intent);
    }
}
