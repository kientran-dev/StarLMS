package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.starlms.starlms.adapter.NotificationAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityMainBinding;
import com.starlms.starlms.databinding.ItemFeatureBinding;
import com.starlms.starlms.entity.Course;
import com.starlms.starlms.entity.Grade;
import com.starlms.starlms.entity.Notification;
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
        getSupportActionBar().setTitle(null);

        // Setup Features with appropriate icons
        setupFeature(binding.featureAttendance, "Điểm danh, Xin nghỉ học", android.R.drawable.ic_menu_my_calendar);
        setupFeature(binding.featureGrades, "Bảng điểm học tập", android.R.drawable.ic_menu_agenda);
        setupFeature(binding.featureTasks, "Bài tập trắc nghiệm", android.R.drawable.ic_menu_edit);
        setupFeature(binding.featureSchedule, "Thời khóa biểu & Video", android.R.drawable.ic_menu_today);
        setupFeature(binding.featureStudentInfo, "Thông tin học sinh", android.R.drawable.ic_menu_myplaces);
        setupFeature(binding.featureSurvey, "Khảo sát", android.R.drawable.ic_menu_help);

        // Set feature click listeners
        binding.featureAttendance.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, CoursesActivity.class);
            intent.putExtra(CoursesActivity.EXTRA_MODE, CoursesActivity.MODE_ATTENDANCE);
            startActivity(intent);
        });
        binding.featureStudentInfo.getRoot().setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
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
        binding.featureSurvey.getRoot().setOnClickListener(v -> startActivity(new Intent(this, SurveyActivity.class)));
        binding.featureSchedule.getRoot().setOnClickListener(v -> startActivity(new Intent(this, CoursesActivity.class)));

        // Setup Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_chat) {
                startActivity(new Intent(this, MessagesActivity.class));
                return true;
            } else if (itemId == R.id.navigation_home) {
                // Already on the home screen, do nothing
                return true;
            }
            return false;
        });


        insertSampleData();
        setupNotificationSlider();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 4000);
        }
        // Deselect the item when returning to the activity
        binding.bottomNavigation.getMenu().findItem(R.id.navigation_chat).setCheckable(false);
        binding.bottomNavigation.getMenu().findItem(R.id.navigation_chat).setChecked(false);
        binding.bottomNavigation.getMenu().findItem(R.id.navigation_home).setChecked(true);

    }

    private void setupFeature(ItemFeatureBinding featureBinding, String text, int iconResId) {
        featureBinding.featureName.setText(text);
        if (iconResId != 0) {
            featureBinding.featureIcon.setImageResource(iconResId);
        } else {
            featureBinding.featureIcon.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }

    private void setupNotificationSlider() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Notification> notifications = db.notificationDao().getAllNotifications();

            runOnUiThread(() -> {
                if (notifications.isEmpty()) return;

                notificationAdapter = new NotificationAdapter(notifications, this);
                binding.viewPagerNotifications.setAdapter(notificationAdapter);

                new TabLayoutMediator(binding.tabLayoutDots, binding.viewPagerNotifications, (tab, position) -> {}).attach();

                sliderRunnable = () -> {
                    if (notificationAdapter.getItemCount() > 0) {
                        int currentItem = binding.viewPagerNotifications.getCurrentItem();
                        int newItem = (currentItem + 1) % notificationAdapter.getItemCount();
                        binding.viewPagerNotifications.setCurrentItem(newItem, true);
                    }
                };

                binding.viewPagerNotifications.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        sliderHandler.removeCallbacks(sliderRunnable);
                        sliderHandler.postDelayed(sliderRunnable, 4000);
                    }
                });
                sliderHandler.postDelayed(sliderRunnable, 4000);
            });
        });
    }

    @Override
    public void onNotificationClick(Notification notification) {
        String content = notification.getMessage();
        String lowerCaseContent = content.toLowerCase();
        if (lowerCaseContent.contains("điểm") || lowerCaseContent.contains("kiểm tra")) {
            content = "Hãy vào mục Bảng điểm để kiểm tra chi tiết nhé!";
        }
        showNotificationDetailsDialog(content);
    }

    private void showNotificationDetailsDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_notification_details, null);
        builder.setView(dialogView);

        TextView contentText = dialogView.findViewById(R.id.tv_dialog_content);
        Button closeButton = dialogView.findViewById(R.id.btn_dialog_close);

        contentText.setText(content);

        AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void insertSampleData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

            if (db.userDao().getAll().isEmpty()) {
                // --- USER: Trần Trung Kiên ---
                User user = new User(0, "student", "password", "S67890", "Trần Trung Kiên", "15/05/2001", "Nam", "0905111222", "Hà Nội", "Phụ huynh của Kiên", "0934567890");
                long userId = db.userDao().insertAndGetId(user);

                // --- TEACHERS ---
                Teacher teacher1 = new Teacher("Mr. John Doe", "john.doe@example.com");
                Teacher teacher2 = new Teacher("Ms. Jane Smith", "jane.smith@example.com");
                long teacher1Id = db.teacherDao().insertAndGetId(teacher1);
                long teacher2Id = db.teacherDao().insertAndGetId(teacher2);

                // --- COURSE 1: IELTS (Offline) ---
                Course course1 = new Course(0, "IELTS Foundation", "offline");
                long course1Id = db.courseDao().insertAndGetId(course1);

                // Sessions for Course 1 (Schedule)
                Calendar cal = Calendar.getInstance();
                cal.set(2025, Calendar.NOVEMBER, 4, 18, 0);
                Session session1 = new Session(cal.getTimeInMillis(), "Intro to IELTS", (int) course1Id, (int) teacher1Id, "Room 101");
                cal.set(2025, Calendar.NOVEMBER, 6, 18, 0);
                Session session2 = new Session(cal.getTimeInMillis(), "Listening & Speaking", (int) course1Id, (int) teacher1Id, "Room 101");
                db.sessionDao().insertAll(session1, session2);

                // Tests & Grades for Course 1
                Test test1_c1 = new Test(0, "Vocabulary Test 1", "Family and daily routine topics.", 10, (int) course1Id);
                long test1_c1_Id = db.testDao().insertAndGetId(test1_c1);
                db.gradeDao().insertAll(new Grade(0, 8.5, (int) userId, (int) test1_c1_Id));
                Question q1_t1_c1 = new Question(0, "The son of your uncle is your ____?", "Brother", "Cousin", "Nephew", "Son", 2, (int) test1_c1_Id);

                Test test2_c1 = new Test(0, "Mid-term Reading Test", "Complete reading passage and answer questions.", 60, (int) course1Id);
                long test2_c1_Id = db.testDao().insertAndGetId(test2_c1);
                Question q1_t2_c1 = new Question(0, "What is the main idea of the first paragraph?", "Topic A", "Topic B", "Topic C", "Topic D", 1, (int) test2_c1_Id);
                Question q2_t2_c1 = new Question(0, "According to the passage, why did the event occur?", "Reason A", "Reason B", "Reason C", "Reason D", 3, (int) test2_c1_Id);

                // --- COURSE 2: TOEIC (Online) ---
                Course course2 = new Course(0, "TOEIC Target 550", "online");
                long course2Id = db.courseDao().insertAndGetId(course2);

                // Sessions for Course 2 (Video Links)
                Session session1_c2 = new Session(0, "Part 1: Photographs", (int) course2Id, (int) teacher2Id, "https://www.youtube.com/watch?v=example1");
                Session session2_c2 = new Session(0, "Part 2: Question-Response", (int) course2Id, (int) teacher2Id, "https://www.youtube.com/watch?v=example2");
                db.sessionDao().insertAll(session1_c2, session2_c2);

                // Tests & Grades for Course 2
                Test test1_c2 = new Test(0, "Grammar Review 1", "Incomplete Sentences practice.", 20, (int) course2Id);
                long test1_c2_Id = db.testDao().insertAndGetId(test1_c2);
                db.gradeDao().insertAll(new Grade(0, 75, (int) userId, (int) test1_c2_Id));
                Question q1_t1_c2 = new Question(0, "The manager ____ the report yesterday.", "will finish", "finishes", "finished", "has finished", 3, (int) test1_c2_Id);

                Test test2_c2 = new Test(0, "Listening Comprehension 1", "Listen and choose the best response.", 25, (int) course2Id);
                db.testDao().insertAndGetId(test2_c2);

                // Insert all questions
                db.questionDao().insertAll(q1_t1_c1, q1_t2_c1, q2_t2_c1, q1_t1_c2);

                // --- SURVEYS ---
                Survey survey1 = new Survey("Đánh giá chất lượng dịch vụ", "Vui lòng chia sẻ cảm nhận của bạn về trải nghiệm học tập tại trung tâm.");
                Survey survey2 = new Survey("Đánh giá giáo viên - Ms. Jane Smith", "Chia sẻ phản hồi của bạn về giảng viên Jane Smith.");
                db.surveyDao().insertAll(survey1, survey2);

                // --- NOTIFICATIONS ---
                Notification notif1 = new Notification("Lịch học lớp IELTS Foundation Thứ 5 (07/11) sẽ được dời sang Chủ Nhật (10/11) cùng giờ.");
                Notification notif2 = new Notification("Link video mới cho bài học TOEIC 'Part 3: Conversations' đã được cập nhật.");
                Notification notif3 = new Notification("Chúc mừng bạn Trần Trung Kiên đã đạt thành tích cao trong kỳ thi vừa qua! Hãy kiểm tra điểm trong mục Bảng điểm.");
                Notification notif4 = new Notification("Điểm bài kiểm tra Grammar Review 1 đã được cập nhật.");
                db.notificationDao().insertAll(notif1, notif2, notif3, notif4);
            }
        });
    }
}
