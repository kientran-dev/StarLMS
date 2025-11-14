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
import com.starlms.starlms.entity.Survey;
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

        // Set feature names
        setupFeature(binding.featureAttendance, "Điểm danh, Xin nghỉ học");
        setupFeature(binding.featureGrades, "Bảng điểm học tập");
        setupFeature(binding.featureTasks, "Bài tập trắc nghiệm");
        setupFeature(binding.featureSchedule, "Thời khóa biểu lớp học");
        setupFeature(binding.featureStudentInfo, "Thông tin học sinh");
        setupFeature(binding.featureSurvey, "Khảo sát");

        // Set click listeners
        binding.featureStudentInfo.getRoot().setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        binding.featureAttendance.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, CoursesActivity.class);
            intent.putExtra(CoursesActivity.EXTRA_MODE, CoursesActivity.MODE_ATTENDANCE);
            startActivity(intent);
        });
        binding.featureGrades.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, CoursesActivity.class);
            intent.putExtra(CoursesActivity.EXTRA_MODE, CoursesActivity.MODE_GRADES);
            startActivity(intent);
        });
        binding.featureTasks.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, CoursesActivity.class);
            intent.putExtra(CoursesActivity.EXTRA_MODE, CoursesActivity.MODE_ASSIGNMENTS);
            startActivity(intent);
        });

        // Set Survey click listener
        binding.featureSurvey.getRoot().setOnClickListener(v -> startActivity(new Intent(this, SurveyActivity.class)));

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

            // User
            User user = new User();
            user.setUsername("student");
            user.setPassword("password");
            user.setStudentId("S12345");
            user.setFullName("Lê Anh Tạo");
            long userId = db.userDao().insertAndGetId(user);

            // Teachers
            Teacher teacher1 = new Teacher("John Doe", "john.doe@example.com");
            Teacher teacher2 = new Teacher("Jane Smith", "jane.smith@example.com");
            long teacher1Id = db.teacherDao().insertAndGetId(teacher1);
            long teacher2Id = db.teacherDao().insertAndGetId(teacher2);

            // Offline Course
            Course course1 = new Course(0, "IELTS Foundation", "offline");
            long course1Id = db.courseDao().insertAndGetId(course1);
            Calendar cal = Calendar.getInstance();
            cal.set(2025, Calendar.NOVEMBER, 4);
            Session session1 = new Session(cal.getTimeInMillis(), "Intro to IELTS", (int) course1Id, (int) teacher1Id, "Room 101");
            cal.set(2025, Calendar.NOVEMBER, 6);
            Session session2 = new Session(cal.getTimeInMillis(), "Listening & Speaking", (int) course1Id, (int) teacher1Id, "Room 101");
            db.sessionDao().insertAll(session1, session2);

            // Online Course
            Course course2 = new Course(0, "TOEIC Online", "online");
            long course2Id = db.courseDao().insertAndGetId(course2);

            // Surveys
            Survey survey1 = new Survey("Đánh giá chất lượng dịch vụ", "Vui lòng chia sẻ cảm nhận của bạn về trải nghiệm học tập tại trung tâm.");
            Survey survey2 = new Survey("Đánh giá năng lực học viên", "Tự đánh giá về sự tiến bộ của bạn trong khóa học vừa qua.");
            Survey survey3 = new Survey("Đánh giá giáo viên", "Chia sẻ phản hồi của bạn về giảng viên đã dạy bạn.");
            survey3.setCompleted(true); // Mark one as already completed
            Survey survey4 = new Survey("Đánh giá cơ sở vật chất", "Chúng tôi muốn biết ý kiến của bạn về phòng học, trang thiết bị và các tiện ích khác.");
            db.surveyDao().insertAll(survey1, survey2, survey3, survey4);

            // Update Schedule click listener on the main thread after data is ready
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
