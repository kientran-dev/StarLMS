package com.starlms.starlms;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityRequestLeaveBinding;
import com.starlms.starlms.entity.Attendance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestLeaveActivity extends AppCompatActivity {

    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String EXTRA_USER_ID = "EXTRA_USER_ID"; // Assuming you have a way to get current user ID

    private ActivityRequestLeaveBinding binding;
    private int sessionId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestLeaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionId = getIntent().getIntExtra(EXTRA_SESSION_ID, -1);
        // For now, we'll hardcode the user ID to 1 as we don't have a login system yet.
        userId = 1;

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
        if (sessionId == -1 || userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin buổi học", Toast.LENGTH_SHORT).show();
            return;
        }

        String reason = binding.spinnerReasons.getSelectedItem().toString();
        String notes = binding.editTextNotes.getText().toString();
        String fullReason = reason;
        if (reason.equalsIgnoreCase("Khác") && !notes.isEmpty()) {
            fullReason = notes;
        }

        Attendance attendance = new Attendance();
        attendance.setSessionId(sessionId);
        attendance.setUserId(userId);
        attendance.setStatus("LEAVE_REQUESTED");
        attendance.setReason(fullReason);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            db.attendanceDao().insert(attendance);
            runOnUiThread(() -> {
                Toast.makeText(this, "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                finish(); // Go back to the previous screen
            });
        });
    }
}
