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
import com.starlms.starlms.entity.Grade;
import com.starlms.starlms.entity.Notification;
import com.starlms.starlms.entity.Question;
import com.starlms.starlms.entity.Test;
import com.starlms.starlms.entity.User;

import java.util.Date;
import java.util.List;
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
        seedDatabase();

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

    private void seedDatabase() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

            // --- Seed Notifications ---
            if (db.notificationDao().getAllOrderedByIdDesc().isEmpty()) {
                db.notificationDao().insert(new Notification("Chào mừng bạn đã đến với hệ thống học tập StarLMS. Hãy khám phá các tính năng ngay!"));
                db.notificationDao().insert(new Notification("Lịch học môn Phát triển ứng dụng TMĐT đã được cập nhật. Vui lòng kiểm tra lại."));
            }

            // --- Seed Tests, Questions, and Grades ---
            if (db.testDao().getTestsForCourse(1).isEmpty()) {
                // Test 1 for Course 1
                Test test1 = new Test(0, "Bài kiểm tra giữa kỳ", "Kiểm tra kiến thức cơ bản", 100, 1);
                long test1Id = db.testDao().insert(test1);

                db.questionDao().insert(new Question(0, "Đâu là thủ đô của Việt Nam?", "Hà Nội", "Đà Nẵng", "TP.HCM", "Hải Phòng", 1, (int)test1Id));
                db.questionDao().insert(new Question(0, "Ngôn ngữ lập trình chính cho Android là gì?", "Kotlin", "Swift", "Java", "C#", 1, (int)test1Id));
                db.questionDao().insert(new Question(0, "Đâu là một widget trong Android?", "TextView", "Array", "String", "Integer", 1, (int)test1Id));

                // Add a grade for user 1 on test 1
                db.gradeDao().insert(new Grade(0, 85.5, 1, (int)test1Id));

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
        // Start the slider when the activity is resumed
        startSlider();
        binding.bottomNavigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the slider when the activity is paused
        stopSlider();
    }

    private void setupFeature(ItemFeatureBinding featureBinding, String text, int iconResId) {
        featureBinding.featureName.setText(text);
        if (iconResId != 0) { // Corrected this line
            featureBinding.featureIcon.setImageResource(iconResId);
        } else {
            featureBinding.featureIcon.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }

    private void setupNotificationSlider() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Notification> notifications = db.notificationDao().getAllOrderedByIdDesc();
            runOnUiThread(() -> {
                notificationAdapter = new NotificationAdapter(notifications, this);
                binding.viewPagerNotifications.setAdapter(notificationAdapter);

                new TabLayoutMediator(binding.tabLayoutDots, binding.viewPagerNotifications, (tab, position) -> {
                    // No-op
                }).attach();

                if (notifications.size() > 1) {
                    sliderRunnable = () -> {
                        int currentItem = binding.viewPagerNotifications.getCurrentItem();
                        int nextItem = currentItem == notificationAdapter.getItemCount() - 1 ? 0 : currentItem + 1;
                        binding.viewPagerNotifications.setCurrentItem(nextItem, true);
                        // Post the runnable again for a continuous loop
                        sliderHandler.postDelayed(sliderRunnable, 4000);
                    };
                    // Start the auto-scroll
                    startSlider();
                }
            });
        });
    }

    private void startSlider() {
        // Only start if the runnable has been created
        if (sliderRunnable != null) {
            // Remove any existing callbacks to prevent duplicates
            sliderHandler.removeCallbacks(sliderRunnable);
            // Post the runnable with a 4-second delay
            sliderHandler.postDelayed(sliderRunnable, 4000);
        }
    }

    private void stopSlider() {
        // Stop the runnable by removing any pending callbacks
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @Override
    public void onNotificationClick(Notification notification) {
        showNotificationDetailsDialog(notification.getMessage());
    }

    private void showNotificationDetailsDialog(String content) {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage(content)
                .setPositiveButton("Đóng", null)
                .show();
    }

}
