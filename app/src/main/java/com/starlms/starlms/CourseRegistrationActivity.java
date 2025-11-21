package com.starlms.starlms;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.CourseRegistrationAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityCourseRegistrationBinding;
import com.starlms.starlms.entity.UserCourseCrossRef;
import com.starlms.starlms.model.CourseWithTeacher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseRegistrationActivity extends AppCompatActivity {

    private ActivityCourseRegistrationBinding binding;
    private CourseRegistrationAdapter adapter;
    private AppDatabase db;
    private ExecutorService executor;
    private long currentUserId = 1; // TODO: Replace with actual logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();

        setSupportActionBar(binding.toolbarCourseRegistration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đăng ký khóa học");
        }
        binding.toolbarCourseRegistration.setNavigationOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        loadCourses();
    }

    private void setupRecyclerView() {
        binding.recyclerViewRegistrationCourses.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCourses() {
        executor.execute(() -> {
            List<CourseWithTeacher> courses = db.courseDao().getUnregisteredCoursesForUser(currentUserId);
            runOnUiThread(() -> {
                // SỬA Ở ĐÂY: Dùng constructor mới
                adapter = new CourseRegistrationAdapter(courses, this::registerForCourse);
                binding.recyclerViewRegistrationCourses.setAdapter(adapter);
            });
        });
    }

    private void registerForCourse(CourseWithTeacher course) {
        UserCourseCrossRef crossRef = new UserCourseCrossRef(currentUserId, course.getCourse().getCourseId());
        executor.execute(() -> {
            db.userCourseCrossRefDao().insert(crossRef);
            runOnUiThread(() -> {
                Toast.makeText(this, "Đăng ký thành công khóa học: " + course.getCourse().getName(), Toast.LENGTH_SHORT).show();
                // SỬA Ở ĐÂY: Tải lại danh sách để đảm bảo tính nhất quán
                loadCourses();
            });
        });
    }
}
