package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.SessionAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivitySessionsBinding;
import com.starlms.starlms.entity.Session;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionsActivity extends AppCompatActivity implements SessionAdapter.OnSessionInteractionListener {

    public static final String EXTRA_COURSE_ID = "COURSE_ID";
    public static final String EXTRA_COURSE_NAME = "COURSE_NAME";
    public static final String EXTRA_COURSE_TYPE = "COURSE_TYPE";

    private ActivitySessionsBinding binding;
    private SessionAdapter adapter;
    private int courseId;
    private String courseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySessionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        courseType = getIntent().getStringExtra(EXTRA_COURSE_TYPE);
        String courseName = getIntent().getStringExtra(EXTRA_COURSE_NAME);

        setSupportActionBar(binding.toolbarSessions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarSessions.setNavigationOnClickListener(v -> onBackPressed());

        if ("online".equalsIgnoreCase(courseType)) {
            getSupportActionBar().setTitle(courseName != null ? "Video bài giảng: " + courseName : "Videos");
        } else {
            getSupportActionBar().setTitle(courseName != null ? "Lịch học: " + courseName : "Buổi học");
        }

        setupRecyclerView();
        loadSessions();
    }

    private void setupRecyclerView() {
        binding.recyclerViewSessions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadSessions() {
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(takePictureIntent);
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
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        startActivity(intent);
    }
}
