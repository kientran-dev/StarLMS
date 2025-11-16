package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.SessionAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivitySessionsBinding;
import com.starlms.starlms.entity.Attendance;
import com.starlms.starlms.entity.Session;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SessionsActivity extends AppCompatActivity implements SessionAdapter.OnSessionInteractionListener {

    public static final String EXTRA_COURSE_ID = "COURSE_ID";
    public static final String EXTRA_COURSE_NAME = "COURSE_NAME";
    public static final String EXTRA_COURSE_TYPE = "COURSE_TYPE";

    private ActivitySessionsBinding binding;
    private SessionAdapter adapter;
    private AppDatabase db;
    private ExecutorService executor;
    private int courseId;
    private String courseType;
    private long currentUserId = 1; // TODO: Replace with actual logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySessionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void setupRecyclerView() {
        binding.recyclerViewSessions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {
        executor.execute(() -> {
            List<Session> sessions = db.sessionDao().getSessionsForCourse(courseId);
            List<Attendance> attendanceList = db.attendanceDao().getAttendanceForUserInCourse((int) currentUserId, courseId);
            Map<Integer, Attendance> attendanceMap = attendanceList.stream()
                    .collect(Collectors.toMap(Attendance::getSessionId, att -> att));

            runOnUiThread(() -> {
                adapter = new SessionAdapter(sessions, attendanceMap, courseType, this);
                binding.recyclerViewSessions.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onCheckInClick(Session session) {
        Attendance attendance = new Attendance(0, (int)currentUserId, session.getSessionId(), "PRESENT", "");
        insertAttendanceAndReload(attendance);
    }

    @Override
    public void onRequestLeaveClick(Session session) {
        // SỬA Ở ĐÂY: Truyền cả courseId và sessionId
        Intent intent = new Intent(this, RequestLeaveActivity.class);
        intent.putExtra(RequestLeaveActivity.EXTRA_COURSE_ID, (long) courseId);
        intent.putExtra(RequestLeaveActivity.EXTRA_SESSION_ID, session.getSessionId());
        startActivity(intent);
    }

    private void insertAttendanceAndReload(Attendance attendance) {
        executor.execute(() -> {
            db.attendanceDao().insert(attendance);
            runOnUiThread(this::loadData);
        });
        Toast.makeText(this, "Đã ghi nhận", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVideoClick(Session session) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        startActivity(intent);
    }
}
