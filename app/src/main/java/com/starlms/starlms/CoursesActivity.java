package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.CourseAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityCoursesBinding;
import com.starlms.starlms.entity.Course;
import com.starlms.starlms.model.UserWithCourses;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoursesActivity extends AppCompatActivity implements CourseAdapter.OnCourseClickListener {

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final int MODE_ATTENDANCE = 1;
    public static final int MODE_GRADES = 2;
    public static final int MODE_ASSIGNMENTS = 3;

    private ActivityCoursesBinding binding;
    private CourseAdapter adapter;
    private int mode;
    private long currentUserId = 1; // TODO: Replace with actual logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoursesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mode = getIntent().getIntExtra(EXTRA_MODE, 0);

        setSupportActionBar(binding.toolbarCourses);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarCourses.setNavigationOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        loadUserCourses();
    }

    private void setupRecyclerView() {
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadUserCourses() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        binding.progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            // SỬA Ở ĐÂY: Lấy danh sách khóa học của user hiện tại
            UserWithCourses userWithCourses = db.userDao().getUserWithCourses(currentUserId);
            List<Course> courses = userWithCourses != null ? userWithCourses.getCourses() : Collections.emptyList();

            runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                adapter = new CourseAdapter(courses, this);
                binding.recyclerViewCourses.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onCourseClick(Course course) {
        Intent intent;
        switch (mode) {
            case MODE_ATTENDANCE:
            case 0: // Default to schedule/video view
                intent = new Intent(this, SessionsActivity.class);
                intent.putExtra(SessionsActivity.EXTRA_COURSE_TYPE, course.getType());
                break;
            case MODE_GRADES:
                intent = new Intent(this, GradesActivity.class);
                break;
            case MODE_ASSIGNMENTS:
                intent = new Intent(this, AssignmentsActivity.class);
                break;
            default:
                return; // Or handle error
        }

        intent.putExtra(SessionsActivity.EXTRA_COURSE_ID, course.getCourseId());
        intent.putExtra(SessionsActivity.EXTRA_COURSE_NAME, course.getName());
        startActivity(intent);
    }
}
