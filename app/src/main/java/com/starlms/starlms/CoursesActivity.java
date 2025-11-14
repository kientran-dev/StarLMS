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
        switch (currentMode) {
            case MODE_GRADES:
                Intent gradesIntent = new Intent(this, GradesActivity.class);
                gradesIntent.putExtra(GradesActivity.EXTRA_COURSE_ID, course.getCourseId());
                gradesIntent.putExtra(GradesActivity.EXTRA_COURSE_NAME, course.getName());
                startActivity(gradesIntent);
                break;
            case MODE_ASSIGNMENTS:
                Intent assignmentsIntent = new Intent(this, AssignmentsActivity.class);
                assignmentsIntent.putExtra("COURSE_ID", course.getCourseId());
                startActivity(assignmentsIntent);
                break;
            case MODE_ATTENDANCE:
                 Intent attendanceIntent = new Intent(this, SessionsActivity.class);
                 attendanceIntent.putExtra(SessionsActivity.EXTRA_COURSE_ID, course.getCourseId());
                 attendanceIntent.putExtra(SessionsActivity.EXTRA_COURSE_NAME, course.getName());
                 attendanceIntent.putExtra(SessionsActivity.EXTRA_COURSE_TYPE, course.getType());
                 startActivity(attendanceIntent);
                 break;
            default: // Browsing for schedule or videos
                if (Objects.equals(course.getType(), "offline")) {
                    Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
                    scheduleIntent.putExtra(ScheduleActivity.EXTRA_COURSE_ID, course.getCourseId());
                    scheduleIntent.putExtra("COURSE_NAME", course.getName()); // Pass the course name
                    startActivity(scheduleIntent);
                } else { // "online"
                    Intent sessionsIntent = new Intent(this, SessionsActivity.class);
                    sessionsIntent.putExtra(SessionsActivity.EXTRA_COURSE_ID, course.getCourseId());
                    sessionsIntent.putExtra(SessionsActivity.EXTRA_COURSE_NAME, course.getName());
                    sessionsIntent.putExtra(SessionsActivity.EXTRA_COURSE_TYPE, course.getType());
                    startActivity(sessionsIntent);
                }
                break;
        }
    }
}
