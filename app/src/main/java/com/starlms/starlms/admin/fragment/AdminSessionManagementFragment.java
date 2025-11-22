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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.starlms.starlms.R;
import com.starlms.starlms.adapter.AdminSessionAdapter;
import com.starlms.starlms.dao.SessionDao;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.entity.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminSessionManagementFragment extends Fragment {

    private static final String ARG_COURSE_ID = "course_id";
    private static final String ARG_COURSE_NAME = "course_name";
    private static final String ARG_COURSE_TYPE = "course_type";

    private long courseId;
    private String courseName;
    private String courseType;

    private SessionDao sessionDao;
    private RecyclerView recyclerView;
    private AdminSessionAdapter adapter;
    private ExecutorService executorService;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public static AdminSessionManagementFragment newInstance(long courseId, String courseName, String courseType) {
        AdminSessionManagementFragment fragment = new AdminSessionManagementFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_COURSE_ID, courseId);
        args.putString(ARG_COURSE_NAME, courseName);
        args.putString(ARG_COURSE_TYPE, courseType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getLong(ARG_COURSE_ID);
            courseName = getArguments().getString(ARG_COURSE_NAME);
            courseType = getArguments().getString(ARG_COURSE_TYPE);
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
        
        // SỬA Ở ĐÂY: Truyền courseType vào adapter
        adapter = new AdminSessionAdapter(courseType);
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
                .setTitle(session.getTitle())
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

    private void setupDatePicker(EditText dateField) {
        dateField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                calendar.set(year, month, dayOfMonth);
                dateField.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private void showSessionDialog(@Nullable final Session session) {
        boolean isUpdate = (session != null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isUpdate ? "Cập nhật Buổi học" : "Thêm Buổi học");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.admin_dialog_add_update_session, null);
        builder.setView(dialogView);

        // --- Find Views ---
        EditText etName = dialogView.findViewById(R.id.et_session_name);
        EditText etLocation = dialogView.findViewById(R.id.et_session_location);
        TextInputLayout layoutLocation = dialogView.findViewById(R.id.layout_session_location);
        EditText etTime = dialogView.findViewById(R.id.et_session_time);

        SwitchMaterial switchRecurring = dialogView.findViewById(R.id.switch_recurring);
        LinearLayout layoutRecurringOptions = dialogView.findViewById(R.id.layout_recurring_options);
        TextInputLayout layoutSingleDate = dialogView.findViewById(R.id.layout_single_date);
        EditText etSingleDate = dialogView.findViewById(R.id.et_session_date);
        EditText etStartDate = dialogView.findViewById(R.id.et_start_date);
        EditText etEndDate = dialogView.findViewById(R.id.et_end_date);

        List<CheckBox> dayCheckBoxes = new ArrayList<>();
        dayCheckBoxes.add(dialogView.findViewById(R.id.cb_sunday));
        dayCheckBoxes.add(dialogView.findViewById(R.id.cb_monday));
        dayCheckBoxes.add(dialogView.findViewById(R.id.cb_tuesday));
        dayCheckBoxes.add(dialogView.findViewById(R.id.cb_wednesday));
        dayCheckBoxes.add(dialogView.findViewById(R.id.cb_thursday));
        dayCheckBoxes.add(dialogView.findViewById(R.id.cb_friday));
        dayCheckBoxes.add(dialogView.findViewById(R.id.cb_saturday));

        // --- Initial UI State ---
        if ("online".equalsIgnoreCase(courseType)) {
            layoutLocation.setHint("Link video");
        } else {
            layoutLocation.setHint("Địa điểm");
        }

        switchRecurring.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRecurringOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            layoutSingleDate.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });
        
        if(isUpdate) {
             switchRecurring.setVisibility(View.GONE);
        }

        // --- Date & Time Picker Logic ---
        setupDatePicker(etSingleDate);
        setupDatePicker(etStartDate);
        setupDatePicker(etEndDate);
        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                etTime.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // --- Populate Data for Update ---
        if (isUpdate) {
            etName.setText(session.getTitle());
            etLocation.setText(session.getClassroom());
            if (session.getSessionDate() > 0) {
                etSingleDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(session.getSessionDate())));
                etTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(session.getSessionDate())));
            }
        }

        builder.setPositiveButton(isUpdate ? "Cập nhật" : "Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String timeStr = etTime.getText().toString();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "Vui lòng nhập tên buổi học", Toast.LENGTH_SHORT).show();
                return;
            }
             if (TextUtils.isEmpty(timeStr)) {
                Toast.makeText(getContext(), "Vui lòng nhập giờ học", Toast.LENGTH_SHORT).show();
                return;
            }
            
            executorService.execute(() -> {
                List<Session> sessionsToInsert = new ArrayList<>();
                try {
                    if (isUpdate) { // Update logic
                        String singleDateStr = etSingleDate.getText().toString();
                         if (TextUtils.isEmpty(singleDateStr)) {
                            mainThreadHandler.post(() -> Toast.makeText(getContext(), "Vui lòng nhập ngày học", Toast.LENGTH_SHORT).show());
                            return;
                        }
                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        Date date = dateTimeFormat.parse(singleDateStr + " " + timeStr);
                        session.setTitle(name);
                        session.setClassroom(location);
                        session.setSessionDate(date.getTime());
                        sessionDao.update(session);
                    } else { // Insert logic
                        if (switchRecurring.isChecked()) { // Recurring insert
                            String startDateStr = etStartDate.getText().toString();
                            String endDateStr = etEndDate.getText().toString();
                            if(TextUtils.isEmpty(startDateStr)) {
                                mainThreadHandler.post(() -> Toast.makeText(getContext(), "Vui lòng nhập ngày bắt đầu", Toast.LENGTH_SHORT).show());
                                return;
                            }
                            if(TextUtils.isEmpty(endDateStr)) {
                                mainThreadHandler.post(() -> Toast.makeText(getContext(), "Vui lòng nhập ngày kết thúc", Toast.LENGTH_SHORT).show());
                                return;
                            }

                            boolean dayIsSelected = false;
                            for(CheckBox cb : dayCheckBoxes) {
                                if(cb.isChecked()) {
                                    dayIsSelected = true;
                                    break;
                                }
                            }
                            if(!dayIsSelected) {
                                mainThreadHandler.post(() -> Toast.makeText(getContext(), "Vui lòng chọn ít nhất một ngày trong tuần", Toast.LENGTH_SHORT).show());
                                return;
                            }

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            Calendar startCal = Calendar.getInstance();
                            startCal.setTime(dateFormat.parse(startDateStr));
                            Calendar endCal = Calendar.getInstance();
                            endCal.setTime(dateFormat.parse(endDateStr));

                             String[] timeParts = timeStr.split(":");
                            startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
                            startCal.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));

                            while (!startCal.after(endCal)) {
                                int dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK); // SUNDAY=1, MONDAY=2, ...
                                if (dayCheckBoxes.get(dayOfWeek-1).isChecked()) {
                                    int placeholderTeacherId = 1;
                                    sessionsToInsert.add(new Session(startCal.getTimeInMillis(), name, (int)courseId, placeholderTeacherId, location));
                                }
                                startCal.add(Calendar.DAY_OF_MONTH, 1);
                            }
                            sessionDao.insertAll(sessionsToInsert.toArray(new Session[0]));

                        } else { // Single insert
                             String singleDateStr = etSingleDate.getText().toString();
                             if (TextUtils.isEmpty(singleDateStr)) {
                                mainThreadHandler.post(() -> Toast.makeText(getContext(), "Vui lòng nhập ngày học", Toast.LENGTH_SHORT).show());
                                return;
                            }
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            Date date = dateTimeFormat.parse(singleDateStr + " " + timeStr);
                            int placeholderTeacherId = 1;
                            Session newSession = new Session(date.getTime(), name, (int)courseId, placeholderTeacherId, location);
                            sessionDao.insert(newSession);
                        }
                    }
                    mainThreadHandler.post(() -> {
                        Toast.makeText(getContext(), sessionsToInsert.size() > 1 ? "Đã thêm " + sessionsToInsert.size() + " buổi học" : (isUpdate ? "Đã cập nhật" : "Đã thêm"), Toast.LENGTH_SHORT).show();
                        loadSessions();
                    });
                } catch (ParseException e) {
                     mainThreadHandler.post(() -> Toast.makeText(getContext(), "Định dạng ngày/giờ không hợp lệ!", Toast.LENGTH_SHORT).show());
                }
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
