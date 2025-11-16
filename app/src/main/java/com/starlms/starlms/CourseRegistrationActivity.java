package com.starlms.starlms;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.starlms.starlms.adapter.CourseRegistrationAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.entity.UserCourseCrossRef;
import com.starlms.starlms.model.CourseWithTeacher;
import com.starlms.starlms.model.UserWithCourses;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CourseRegistrationActivity extends AppCompatActivity implements CourseRegistrationAdapter.OnRegisterClickListener {

    private RecyclerView recyclerView;
    private CourseRegistrationAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;
    private long currentUserId = 1; // TODO: Replace with actual logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);

        db = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_course_registration);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycler_view_registration_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        loadData();
    }

    private void loadData() {
        executorService.execute(() -> {
            // 1. Get all courses available
            List<CourseWithTeacher> allCourses = db.courseDao().getCoursesWithTeachers();

            // 2. Get courses already registered by the user
            UserWithCourses userWithCourses = db.userDao().getUserWithCourses(currentUserId);
            Set<Integer> registeredCourseIds = userWithCourses != null
                    ? userWithCourses.getCourses().stream().map(c -> c.getCourseId()).collect(Collectors.toSet())
                    : Collections.emptySet();

            runOnUiThread(() -> {
                adapter = new CourseRegistrationAdapter(allCourses, new HashSet<>(registeredCourseIds), this);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onRegisterClick(CourseWithTeacher course) {
        executorService.execute(() -> {
            UserCourseCrossRef crossRef = new UserCourseCrossRef(currentUserId, course.getCourse().getCourseId());
            db.userCourseCrossRefDao().insert(crossRef);

            runOnUiThread(() -> {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                // Update adapter to disable the button
                adapter.updateRegistrationStatus(course.getCourse().getCourseId());
            });
        });
    }
}
