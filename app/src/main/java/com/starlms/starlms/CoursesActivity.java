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
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoursesActivity extends AppCompatActivity implements CourseAdapter.OnCourseClickListener {

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String MODE_ATTENDANCE = "attendance";
    public static final String MODE_GRADES = "grades";
    public static final String MODE_ASSIGNMENTS = "assignments";
    public static final String MODE_SCHEDULE_OR_VIDEO = "schedule_or_video";

    private ActivityCoursesBinding binding;
    private CourseAdapter adapter;
    private String mode;
    private long currentUserId = 1; // TODO: Replace with actual logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoursesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mode = getIntent().getStringExtra(EXTRA_MODE);

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
        if (mode == null) return;

        if (Objects.equals(mode, MODE_SCHEDULE_OR_VIDEO)) {
            if ("offline".equalsIgnoreCase(course.getType())) {
                Intent intent = new Intent(this, ScheduleActivity.class);
                intent.putExtra(ScheduleActivity.EXTRA_COURSE_ID, course.getCourseId());
                intent.putExtra("COURSE_NAME", course.getName());
                startActivity(intent);
            } else if ("online".equalsIgnoreCase(course.getType())) {
                Intent intent = new Intent(this, SessionsActivity.class);
                intent.putExtra(SessionsActivity.EXTRA_COURSE_ID, course.getCourseId());
                intent.putExtra(SessionsActivity.EXTRA_COURSE_NAME, course.getName());
                startActivity(intent);
            }
        } else {
            Intent intent;
            switch (mode) {
                case MODE_ATTENDANCE:
                    intent = new Intent(this, RequestLeaveActivity.class); // Or whatever activity is for attendance
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
            intent.putExtra("COURSE_ID", course.getCourseId());
            intent.putExtra("COURSE_NAME", course.getName());
            startActivity(intent);
        }
    }
}
