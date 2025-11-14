package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityMainBinding;
import com.starlms.starlms.databinding.ItemFeatureBinding;
import com.starlms.starlms.entity.Course;
import com.starlms.starlms.entity.Grade;
import com.starlms.starlms.entity.Question;
import com.starlms.starlms.entity.Session;
import com.starlms.starlms.entity.Teacher;
import com.starlms.starlms.entity.Test;
import com.starlms.starlms.entity.User;

import java.util.Calendar;
import java.util.List;
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
        setupFeature(binding.featureTasks, "Bài tập trắc nghiệm");
        setupFeature(binding.featureSchedule, "Thời khóa biểu lớp học");
        setupFeature(binding.featureStudentInfo, "Thông tin học sinh");
        setupFeature(binding.featureSurvey, "Khảo sát");

        // Set other click listeners
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

        binding.featureTasks.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CoursesActivity.class);
            intent.putExtra(CoursesActivity.EXTRA_MODE, CoursesActivity.MODE_ASSIGNMENTS);
            startActivity(intent);
        });

        // The click listener for the schedule will be set after the sample data is inserted

        insertSampleData();
    }

    private void setupFeature(ItemFeatureBinding featureBinding, String text) {
        featureBinding.featureName.setText(text);
    }

    private void insertSampleData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            db.clearAllTables();

            // Sample User
            User user = new User();
            user.setUsername("student");
            user.setPassword("password");
            user.setStudentId("S12345");
            user.setFullName("Lê Anh Tạo");
            user.setDateOfBirth("18/11/2004");
            user.setGender("Nam");
            user.setPhone("0123456789");
            user.setAddress("Hanoi, Vietnam");
            user.setContactName("Phụ huynh của Tạo");
            user.setContactPhone("0987654321");
            long userId = db.userDao().insertAndGetId(user);

            // Sample Teachers
            Teacher teacher1 = new Teacher("John Doe", "john.doe@example.com");
            Teacher teacher2 = new Teacher("Jane Smith", "jane.smith@example.com");
            long teacher1Id = db.teacherDao().insertAndGetId(teacher1);
            long teacher2Id = db.teacherDao().insertAndGetId(teacher2);

            // --- Course 1: IELTS Foundation (Offline) ---
            Course course1 = new Course(0, "IELTS Foundation", "offline");
            long course1Id = db.courseDao().insertAndGetId(course1);

            Calendar cal = Calendar.getInstance();

            // Sessions for Course 1 (Offline)
            cal.set(2025, Calendar.NOVEMBER, 4, 9, 0);
            Session session1 = new Session(cal.getTimeInMillis(), "Session 1: Introduction to IELTS", (int) course1Id, (int) teacher1Id, "Room 101");
            cal.set(2025, Calendar.NOVEMBER, 6, 14, 0);
            Session session2 = new Session(cal.getTimeInMillis(), "Session 2: Listening & Speaking", (int) course1Id, (int) teacher1Id, "Room 101");
            cal.set(2025, Calendar.NOVEMBER, 8, 10, 30);
            Session session3 = new Session(cal.getTimeInMillis(), "Session 3: Reading & Writing", (int) course1Id, (int) teacher2Id, "Room 202");
            db.sessionDao().insertAll(session1, session2, session3);

            // Tests for Course 1
            Test c1Test1 = new Test(0, "Grammar Test 1", "Simple present and past tenses.", 10, (int) course1Id);
            Test c1Test2 = new Test(0, "Vocabulary Test 1", "Family and daily routine topics.", 15, (int) course1Id);
            List<Long> testIds = db.testDao().insertAll(c1Test1, c1Test2);
            long c1Test1Id = testIds.get(0);
            long c1Test2Id = testIds.get(1);

            // Questions for Test 2
            Question q1 = new Question(0, "Which of the following is a synonym for 'mother'?", "Father", "Sister", "Mom", "Brother", 3, (int) c1Test2Id);
            Question q2 = new Question(0, "The son of your uncle is your ____?", "Brother", "Cousin", "Nephew", "Son", 2, (int) c1Test2Id);
            db.questionDao().insertAll(q1, q2);

            // Grades for Course 1
            Grade grade1 = new Grade(0, 8, (int) userId, (int) c1Test1Id);
            db.gradeDao().insertAll(grade1);

            // --- Course 2: TOEIC Online ---
            Course course2 = new Course(0, "TOEIC Online", "online");
            long course2Id = db.courseDao().insertAndGetId(course2);

            // Sessions for Course 2 (Online)
            Session session4 = new Session(System.currentTimeMillis(), "Video 1: Grammar Basics", (int) course2Id, null, null);
            db.sessionDao().insertAll(session4);

            // Update the click listener on the main thread after data is ready
            runOnUiThread(() -> {
                binding.featureSchedule.getRoot().setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                    intent.putExtra(ScheduleActivity.EXTRA_COURSE_ID, (int) course1Id);
                    startActivity(intent);
                });
            });
        });
    }
}
