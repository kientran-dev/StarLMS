package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.CourseAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityCoursesBinding;
import com.starlms.starlms.entity.Course;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoursesActivity extends AppCompatActivity implements CourseAdapter.OnCourseClickListener {

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String MODE_ATTENDANCE = "attendance";
    public static final String MODE_GRADES = "grades";
    public static final String MODE_ASSIGNMENTS = "assignments";

    private ActivityCoursesBinding binding;
    private CourseAdapter adapter;
    private String currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoursesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentMode = getIntent().getStringExtra(EXTRA_MODE);
        if (currentMode == null) {
            currentMode = MODE_ATTENDANCE; // Default mode
        }

        setSupportActionBar(binding.toolbarCourses);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarCourses.setNavigationOnClickListener(v -> onBackPressed());

        if (MODE_GRADES.equals(currentMode)) {
            getSupportActionBar().setTitle("Chọn khóa học để xem điểm");
        } else if (MODE_ASSIGNMENTS.equals(currentMode)) {
            getSupportActionBar().setTitle("Chọn khóa học để xem bài tập");
        } else {
            getSupportActionBar().setTitle("Chọn khóa học để điểm danh");
        }

        setupRecyclerView();
        loadCourses();
    }

    private void setupRecyclerView() {
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCourses() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Course> courses = db.courseDao().getAllCourses();
            runOnUiThread(() -> {
                adapter = new CourseAdapter(courses, this);
                binding.recyclerViewCourses.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onCourseClick(Course course) {
        if (MODE_GRADES.equals(currentMode)) {
            Intent intent = new Intent(this, GradesActivity.class);
            intent.putExtra(GradesActivity.EXTRA_COURSE_ID, course.getCourseId());
            intent.putExtra(GradesActivity.EXTRA_COURSE_NAME, course.getName());
            startActivity(intent);
        } else if (MODE_ASSIGNMENTS.equals(currentMode)) {
            Intent intent = new Intent(this, AssignmentsActivity.class);
            intent.putExtra("COURSE_ID", course.getCourseId());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SessionsActivity.class);
            intent.putExtra(SessionsActivity.EXTRA_COURSE_ID, course.getCourseId());
            intent.putExtra(SessionsActivity.EXTRA_COURSE_NAME, course.getName());
            intent.putExtra(SessionsActivity.EXTRA_COURSE_TYPE, course.getType());
            startActivity(intent);
        }
    }
}
