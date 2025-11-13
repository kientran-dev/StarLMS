package com.starlms.starlms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.SessionAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivitySessionsBinding;
import com.starlms.starlms.entity.Session;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionsActivity extends AppCompatActivity implements SessionAdapter.OnSessionInteractionListener {

    public static final String EXTRA_COURSE_ID = "EXTRA_COURSE_ID";
    public static final String EXTRA_COURSE_NAME = "EXTRA_COURSE_NAME";
    public static final String EXTRA_COURSE_TYPE = "EXTRA_COURSE_TYPE";
    private static final int CAMERA_PERMISSION_CODE = 101;

    private ActivitySessionsBinding binding;
    private SessionAdapter adapter;
    private String courseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySessionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        String courseName = getIntent().getStringExtra(EXTRA_COURSE_NAME);
        courseType = getIntent().getStringExtra(EXTRA_COURSE_TYPE);

        setSupportActionBar(binding.toolbarSessions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(courseName != null ? courseName : "Buổi học");
        binding.toolbarSessions.setNavigationOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        loadSessions(courseId);
    }

    private void setupRecyclerView() {
        binding.recyclerViewSessions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadSessions(int courseId) {
        if (courseId == -1) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Session> sessions = db.sessionDao().getSessionsForCourse(courseId);
            runOnUiThread(() -> {
                adapter = new SessionAdapter(sessions, courseType, this);
                binding.recyclerViewSessions.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onCheckInClick(Session session) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestLeaveClick(Session session) {
        Intent intent = new Intent(this, RequestLeaveActivity.class);
        intent.putExtra(RequestLeaveActivity.EXTRA_SESSION_ID, session.getSessionId());
        startActivity(intent);
    }

    @Override
    public void onVideoClick(Session session) {
        Toast.makeText(this, "Mở video: " + session.getTitle(), Toast.LENGTH_SHORT).show();
        // Here you would start a video player activity
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(cameraIntent);
        } else {
            Toast.makeText(this, "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Quyền truy cập camera bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
