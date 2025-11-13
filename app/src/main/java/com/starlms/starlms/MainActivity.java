package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityMainBinding;
import com.starlms.starlms.databinding.ItemFeatureBinding;
import com.starlms.starlms.entity.Course;
import com.starlms.starlms.entity.Grade;
import com.starlms.starlms.entity.Session;
import com.starlms.starlms.entity.Test;
import com.starlms.starlms.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Set text for each feature
        setupFeature(binding.featureAttendance, "Điểm danh, Xin nghỉ học");
        setupFeature(binding.featureGrades, "Bảng điểm học tập");
        setupFeature(binding.featureTasks, "Nhiệm vụ, bài tập");
        setupFeature(binding.featureSchedule, "Thời khóa biểu lớp học");
        setupFeature(binding.featureStudentInfo, "Thông tin học sinh");
        setupFeature(binding.featureSurvey, "Khảo sát");

        // Set click listeners
        binding.featureStudentInfo.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        binding.featureAttendance.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CoursesActivity.class);
            intent.putExtra(CoursesActivity.EXTRA_MODE, CoursesActivity.MODE_ATTENDANCE);
            startActivity(intent);
        });

        binding.featureGrades.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CoursesActivity.class);
            intent.putExtra(CoursesActivity.EXTRA_MODE, CoursesActivity.MODE_GRADES);
            startActivity(intent);
        });

        // Insert sample data
        insertSampleData();
    }

    private void setupFeature(ItemFeatureBinding featureBinding, String text) {
        featureBinding.featureName.setText(text);
    }

    private void insertSampleData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

            // Clear previous data to ensure consistency
            db.clearAllTables();

            // Sample User
            User user = new User();
            user.setUsername("anhtao");
            user.setPassword("password");
            user.setStudentId("012578132");
            user.setFullName("Lê Anh Tạo");
            user.setDateOfBirth("18/11/2004");
            user.setGender("Nam");
            user.setPhone("012578132");
            user.setAddress("Hà nội");
            user.setContactName("Lê Anh Tạo");
            user.setContactPhone("098728782");
            db.userDao().insertAll(user);
            int userId = db.userDao().getAll().get(0).getId();

            // --- Course 1: IELTS Foundation ---
            Course course1 = new Course(0, "IELTS Foundation", "offline");
            long course1Id = db.courseDao().insertAndGetId(course1);

            // Sessions for Course 1
            Session session1 = new Session(0, System.currentTimeMillis(), "Buổi 1: Introduction", (int) course1Id);
            db.sessionDao().insertAll(session1);

            // Tests for Course 1
            Test c1Test1 = new Test(0, "Test 1", 100, (int) course1Id);
            Test c1Test2 = new Test(0, "Test 2", 100, (int) course1Id);
            Test c1Test3 = new Test(0, "Test 3", 100, (int) course1Id);
            db.testDao().insertAll(c1Test1, c1Test2, c1Test3);

            // Grades for Course 1
            long c1Test1Id = db.testDao().getTestsForCourse((int) course1Id).get(0).getTestId();
            Grade grade1 = new Grade(0, 85.5, userId, (int) c1Test1Id);
            db.gradeDao().insertAll(grade1);

            // --- Course 2: TOEIC Online ---
            Course course2 = new Course(0, "TOEIC Online", "online");
            long course2Id = db.courseDao().insertAndGetId(course2);

            // Sessions for Course 2
            Session session3 = new Session(0, System.currentTimeMillis(), "Video 1: Grammar Basics", (int) course2Id);
            db.sessionDao().insertAll(session3);

            // Tests for Course 2
            Test c2Test1 = new Test(0, "Test 1", 100, (int) course2Id);
            Test c2Test2 = new Test(0, "Test 2", 100, (int) course2Id);
            Test c2Test3 = new Test(0, "Test 3", 100, (int) course2Id);
            db.testDao().insertAll(c2Test1, c2Test2, c2Test3);
        });
    }
}
