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
import java.util.Objects;
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
            currentMode = ""; 
        }

        setSupportActionBar(binding.toolbarCourses);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarCourses.setNavigationOnClickListener(v -> onBackPressed());

        switch (currentMode) {
            case MODE_GRADES:
                getSupportActionBar().setTitle("Chọn khóa học để xem điểm");
                break;
            case MODE_ASSIGNMENTS:
                getSupportActionBar().setTitle("Chọn khóa học để xem bài tập");
                break;
            case MODE_ATTENDANCE:
                 getSupportActionBar().setTitle("Chọn khóa học để điểm danh");
                 break;
            default:
                getSupportActionBar().setTitle("Chọn khóa học");
                break;
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
        if (Objects.equals(course.getType(), "online")) {
            Intent intent = new Intent(this, SessionsActivity.class);
            intent.putExtra(SessionsActivity.EXTRA_COURSE_ID, course.getCourseId());
            intent.putExtra(SessionsActivity.EXTRA_COURSE_NAME, course.getName());
            intent.putExtra(SessionsActivity.EXTRA_COURSE_TYPE, course.getType());
            startActivity(intent);
        } else { // "offline"
            Intent intent;
            switch (currentMode) {
                case MODE_GRADES:
                    intent = new Intent(this, GradesActivity.class);
                    intent.putExtra(GradesActivity.EXTRA_COURSE_ID, course.getCourseId());
                    intent.putExtra(GradesActivity.EXTRA_COURSE_NAME, course.getName());
                    break;
                case MODE_ASSIGNMENTS:
                    intent = new Intent(this, AssignmentsActivity.class);
                    intent.putExtra("COURSE_ID", course.getCourseId());
                    break;
                case MODE_ATTENDANCE:
                    intent = new Intent(this, SessionsActivity.class);
                    intent.putExtra(SessionsActivity.EXTRA_COURSE_ID, course.getCourseId());
                    intent.putExtra(SessionsActivity.EXTRA_COURSE_NAME, course.getName());
                    intent.putExtra(SessionsActivity.EXTRA_COURSE_TYPE, course.getType());
                    break;
                default: // Browsing for schedule
                    intent = new Intent(this, ScheduleActivity.class);
                    intent.putExtra(ScheduleActivity.EXTRA_COURSE_ID, course.getCourseId());
                    intent.putExtra("COURSE_NAME", course.getName()); // Pass the course name
                    break;
            }
            startActivity(intent);
        }
    }
}
