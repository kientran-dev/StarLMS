package com.starlms.starlms;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityRequestLeaveBinding;
import com.starlms.starlms.entity.Attendance;
import com.starlms.starlms.entity.LeaveRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestLeaveActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "EXTRA_COURSE_ID";
    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID"; // THÊM DÒNG NÀY
    public static final String EXTRA_USER_ID = "EXTRA_USER_ID";

    private ActivityRequestLeaveBinding binding;
    private long courseId;
    private int sessionId; // THÊM DÒNG NÀY
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestLeaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        courseId = getIntent().getLongExtra(EXTRA_COURSE_ID, -1);
        sessionId = getIntent().getIntExtra(EXTRA_SESSION_ID, -1); // THÊM DÒNG NÀY
        userId = 1; // TODO: Replace with actual logged-in user ID

        setSupportActionBar(binding.toolbarRequestLeave);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarRequestLeave.setNavigationOnClickListener(v -> onBackPressed());

        setupSpinner();

        binding.buttonSendRequest.setOnClickListener(v -> sendRequest());
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.leave_reasons,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerReasons.setAdapter(adapter);
    }

    private void sendRequest() {
        if (courseId == -1 || sessionId == -1 || userId == -1) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin khóa học hoặc buổi học", Toast.LENGTH_SHORT).show();
            return;
        }

        String reason = binding.spinnerReasons.getSelectedItem().toString();
        String notes = binding.editTextNotes.getText().toString();
        String fullReason = reason;
        if (reason.equalsIgnoreCase("Khác") && !notes.isEmpty()) {
            fullReason = notes;
        }

        // SỬA Ở ĐÂY: Tạo cả 2 object
        LeaveRequest leaveRequest = new LeaveRequest(0, courseId, userId, fullReason, System.currentTimeMillis(), "Chưa duyệt");
        Attendance attendanceRecord = new Attendance(0, (int) userId, sessionId, "LEAVE_REQUESTED", fullReason);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            // Ghi vào cả 2 bảng
            db.leaveRequestDao().insert(leaveRequest);
            db.attendanceDao().insert(attendanceRecord);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                finish(); // Quay lại màn hình trước
            });
        });
    }
}
