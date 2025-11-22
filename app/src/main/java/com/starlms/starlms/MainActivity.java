package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.starlms.starlms.adapter.NotificationAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityMainBinding;
import com.starlms.starlms.databinding.ItemFeatureBinding;
import com.starlms.starlms.entity.Question;
import com.starlms.starlms.entity.Test;
import com.starlms.starlms.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private ActivityMainBinding binding;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }

        loadUserProfile();
        addSampleQuizData();

        // Setup Toolbar icons
        binding.profileIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // Setup Features
        setupFeature(binding.featureAttendance, "Điểm danh, Xin nghỉ học", android.R.drawable.ic_menu_my_calendar);
        setupFeature(binding.featureGrades, "Bảng điểm học tập", android.R.drawable.ic_menu_agenda);
        setupFeature(binding.featureTasks, "Bài tập trắc nghiệm", android.R.drawable.ic_menu_edit);
        setupFeature(binding.featureSchedule, "Thời khóa biểu & Video", android.R.drawable.ic_menu_today);
        setupFeature(binding.featureRegisterCourse, "Đăng ký khóa học", android.R.drawable.ic_menu_add);
        setupFeature(binding.featureSurvey, "Khảo sát", android.R.drawable.ic_menu_help);

        // Set feature click listeners
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
        binding.featureSchedule.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, CoursesActivity.class);
            intent.putExtra(CoursesActivity.EXTRA_MODE, CoursesActivity.MODE_SCHEDULE_OR_VIDEO);
            startActivity(intent);
        });
        binding.featureRegisterCourse.getRoot().setOnClickListener(v -> startActivity(new Intent(this, CourseRegistrationActivity.class)));
        binding.featureSurvey.getRoot().setOnClickListener(v -> startActivity(new Intent(this, SurveyActivity.class)));

        // Setup Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_chat) {
                startActivity(new Intent(this, MessagesActivity.class));
                return true;
            } else if (itemId == R.id.navigation_contacts) {
                showFeedbackDialog();
                return true;
            } else if (itemId == R.id.navigation_settings) {
                showLogoutDialog();
                return true;
            }
            return false;
        });

        setupNotificationSlider();
    }

    private void addSampleQuizData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

            // Check if data already exists
            if (db.testDao().getTestsForCourse(1).isEmpty()) {
                // Test 1 for Course 1
                Test test1 = new Test(0, "Bài kiểm tra giữa kỳ", "Kiểm tra kiến thức cơ bản", 100, 1);
                long test1Id = db.testDao().insert(test1);

                db.questionDao().insert(new Question(0, "Đâu là thủ đô của Việt Nam?", "Hà Nội", "Đà Nẵng", "TP.HCM", "Hải Phòng", 1, (int)test1Id));
                db.questionDao().insert(new Question(0, "Ngôn ngữ lập trình chính cho Android là gì?", "Kotlin", "Swift", "Java", "C#", 1, (int)test1Id));
                db.questionDao().insert(new Question(0, "Đâu là một widget trong Android?", "TextView", "Array", "String", "Integer", 1, (int)test1Id));

                // Test 2 for Course 1
                Test test2 = new Test(0, "Bài kiểm tra cuối kỳ", "Kiểm tra kiến thức tổng hợp", 100, 1);
                long test2Id = db.testDao().insert(test2);

                db.questionDao().insert(new Question(0, "Lớp nào được sử dụng để tạo một danh sách cuộn?", "RecyclerView", "ListView", "ScrollView", "Both A and B", 4, (int)test2Id));
            }
        });
    }

    private void loadUserProfile() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            User user = db.userDao().findById(1);

            if (user != null) {
                runOnUiThread(() -> {
                    binding.userName.setText(user.getFullName());
                });
            }
        });
    }


    private void showFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_feedback, null);
        builder.setView(dialogView);

        final EditText feedbackInput = dialogView.findViewById(R.id.feedback_input);
        final Button sendButton = dialogView.findViewById(R.id.send_feedback_button);

        AlertDialog dialog = builder.create();

        sendButton.setOnClickListener(v -> {
            String feedback = feedbackInput.getText().toString();
            if (feedback.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập ý kiến của bạn", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cảm ơn bạn đã gửi phản hồi!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 4000);
        }
        binding.bottomNavigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }

    private void setupFeature(ItemFeatureBinding featureBinding, String text, int iconResId) {
        featureBinding.featureName.setText(text);
        if (iconResId != -1) {
            featureBinding.featureIcon.setImageResource(iconResId);
        } else {
            featureBinding.featureIcon.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }

    private void setupNotificationSlider() {
        // ... (existing code)
    }

    @Override
    public void onNotificationClick(com.starlms.starlms.entity.Notification notification) {
        // ... (existing code)
    }

    private void showNotificationDetailsDialog(String content) {
        // ... (existing code)
    }

}
