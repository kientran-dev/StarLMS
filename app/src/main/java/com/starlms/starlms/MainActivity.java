package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityMainBinding;
import com.starlms.starlms.databinding.ItemFeatureBinding;
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
        setupFeature(binding.featureAttendance, "Điểm danh");
        setupFeature(binding.featureGrades, "Kết quả học tập");
        setupFeature(binding.featureTasks, "Nhiệm vụ, bài tập");
        setupFeature(binding.featureSchedule, "Thời khóa biểu");
        setupFeature(binding.featureStudentInfo, "Thông tin học sinh");
        setupFeature(binding.featureSurvey, "Khảo sát");

        binding.featureStudentInfo.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Insert sample data
        insertSampleUser();
    }

    private void setupFeature(ItemFeatureBinding featureBinding, String text) {
        featureBinding.featureName.setText(text);
    }

    private void insertSampleUser() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            if (db.userDao().getAll().isEmpty()) {
                User user = new User();
                user.setUsername("anhtao");
                user.setPassword("password");
                user.setStudentId("012578132");
                user.setFullName("Kiên Trần");
                user.setDateOfBirth("12/12/2004");
                user.setGender("Nam");
                user.setPhone("012578132");
                user.setAddress("Hà nội");
                user.setContactName("Kiên Trần");
                user.setContactPhone("0456728782");
                db.userDao().insertAll(user);
            }
        });
    }
}
