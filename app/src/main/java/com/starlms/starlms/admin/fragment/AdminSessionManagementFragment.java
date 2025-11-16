package com.starlms.starlms.admin.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.starlms.starlms.R;
import com.starlms.starlms.adapter.AdminSessionAdapter;
import com.starlms.starlms.dao.SessionDao;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.entity.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminSessionManagementFragment extends Fragment {

    private static final String ARG_COURSE_ID = "course_id";
    private static final String ARG_COURSE_NAME = "course_name";

    private long courseId;
    private String courseName;

    private SessionDao sessionDao;
    private RecyclerView recyclerView;
    private AdminSessionAdapter adapter;
    private ExecutorService executorService;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public static AdminSessionManagementFragment newInstance(long courseId, String courseName) {
        AdminSessionManagementFragment fragment = new AdminSessionManagementFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_COURSE_ID, courseId);
        args.putString(ARG_COURSE_NAME, courseName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getLong(ARG_COURSE_ID);
            courseName = getArguments().getString(ARG_COURSE_NAME);
        }
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        sessionDao = db.sessionDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_session_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_session_management);
        toolbar.setTitle(courseName != null ? "Lịch học: " + courseName : "Quản lý Lịch học");
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_view_sessions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdminSessionAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemInteractionListener(this::showOptionsDialog);

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_session);
        fabAdd.setOnClickListener(v -> showSessionDialog(null));

        loadSessions();
    }

    private void loadSessions() {
        executorService.execute(() -> {
            List<Session> sessions = sessionDao.getSessionsForCourse((int) courseId);
            mainThreadHandler.post(() -> adapter.setSessions(sessions));
        });
    }

    private void showOptionsDialog(final Session session) {
        final CharSequence[] options = {"Cập nhật", "Xóa"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Lựa chọn cho: " + session.getTitle())
                .setItems(options, (dialog, item) -> {
                    if (options[item].equals("Cập nhật")) {
                        showSessionDialog(session);
                    } else if (options[item].equals("Xóa")) {
                        deleteSession(session);
                    }
                })
                .show();
    }

    private void deleteSession(final Session session) {
        executorService.execute(() -> {
            sessionDao.delete(session);
            mainThreadHandler.post(() -> {
                Toast.makeText(getContext(), "Đã xóa buổi học", Toast.LENGTH_SHORT).show();
                loadSessions();
            });
        });
    }

    private void showSessionDialog(@Nullable final Session session) {
        boolean isUpdate = (session != null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isUpdate ? "Cập nhật Buổi học" : "Thêm Buổi học");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.admin_dialog_add_update_session, null);
        final EditText etName = dialogView.findViewById(R.id.et_session_name);
        final EditText etDate = dialogView.findViewById(R.id.et_session_date);
        final EditText etTime = dialogView.findViewById(R.id.et_session_time);
        final EditText etLocation = dialogView.findViewById(R.id.et_session_location);
        builder.setView(dialogView);

        // --- Date & Time Picker Logic ---
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (isUpdate && session.getSessionDate() > 0) {
                calendar.setTimeInMillis(session.getSessionDate());
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                calendar.set(year, month, dayOfMonth);
                etDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (isUpdate && session.getSessionDate() > 0) {
                calendar.setTimeInMillis(session.getSessionDate());
            }
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                etTime.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        if (isUpdate) {
            etName.setText(session.getTitle());
            etLocation.setText(session.getClassroom());
            if (session.getSessionDate() > 0) {
                etDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(session.getSessionDate())));
                etTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(session.getSessionDate())));
            }
        }

        builder.setPositiveButton(isUpdate ? "Cập nhật" : "Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String dateStr = etDate.getText().toString();
            String timeStr = etTime.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(timeStr)) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            long sessionTimestamp;
            try {
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date date = dateTimeFormat.parse(dateStr + " " + timeStr);
                sessionTimestamp = date.getTime();
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Định dạng ngày/giờ không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                if (isUpdate) {
                    session.setTitle(name);
                    session.setClassroom(location);
                    session.setSessionDate(sessionTimestamp);
                    sessionDao.update(session);
                } else {
                    int placeholderTeacherId = 1; // Simplification
                    Session newSession = new Session(sessionTimestamp, name, (int)courseId, placeholderTeacherId, location);
                    sessionDao.insert(newSession);
                }
                mainThreadHandler.post(() -> {
                    Toast.makeText(getContext(), isUpdate ? "Đã cập nhật" : "Đã thêm", Toast.LENGTH_SHORT).show();
                    loadSessions();
                });
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
