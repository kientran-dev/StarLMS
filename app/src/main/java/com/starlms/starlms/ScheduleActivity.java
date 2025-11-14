package com.starlms.starlms;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.ScheduleAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityScheduleBinding;
import com.starlms.starlms.databinding.ViewDayOfWeekBinding;
import com.starlms.starlms.entity.Schedule;
import com.starlms.starlms.entity.Session;
import com.starlms.starlms.entity.SessionDetails;
import com.starlms.starlms.entity.Teacher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ScheduleActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "COURSE_ID";

    private ActivityScheduleBinding binding;
    private Calendar currentCalendar;
    private SimpleDateFormat dateTimeFormat;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private List<Schedule> allSchedules;
    private int selectedDay = -1;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        String courseName = getIntent().getStringExtra("COURSE_NAME");

        setSupportActionBar(binding.toolbarSchedule);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(courseName != null ? courseName : "Schedule");
        }

        currentCalendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());

        allSchedules = new ArrayList<>();

        setupRecyclerView();
        setupWeekNavigation();
        setupDayClickListeners();

        loadSchedulesFromDb();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupRecyclerView() {
        binding.recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupWeekNavigation() {
        binding.btnPreviousWeek.setOnClickListener(v -> {
            currentCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            updateWeekView();
        });

        binding.btnNextWeek.setOnClickListener(v -> {
            currentCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            updateWeekView();
        });
    }

    private void setupDayClickListeners() {
        binding.dayMonday.getRoot().setOnClickListener(v -> onDaySelected(Calendar.MONDAY));
        binding.dayTuesday.getRoot().setOnClickListener(v -> onDaySelected(Calendar.TUESDAY));
        binding.dayWednesday.getRoot().setOnClickListener(v -> onDaySelected(Calendar.WEDNESDAY));
        binding.dayThursday.getRoot().setOnClickListener(v -> onDaySelected(Calendar.THURSDAY));
        binding.dayFriday.getRoot().setOnClickListener(v -> onDaySelected(Calendar.FRIDAY));
        binding.daySaturday.getRoot().setOnClickListener(v -> onDaySelected(Calendar.SATURDAY));
        binding.daySunday.getRoot().setOnClickListener(v -> onDaySelected(Calendar.SUNDAY));
    }

    private void onDaySelected(int dayOfWeek) {
        selectedDay = dayOfWeek;
        updateDaySelection();
        updateScheduleList();
    }

    private void loadSchedulesFromDb() {
        if (courseId == -1) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<SessionDetails> sessionDetailsList = db.sessionDao().getSessionDetailsForCourse(courseId);

            allSchedules.clear();
            for (SessionDetails details : sessionDetailsList) {
                Session session = details.getSession();
                Teacher teacher = details.getTeacher();

                Date date = new Date(session.getSessionDate());
                String dateTimeString = dateFormat.format(date) + " - " + timeFormat.format(date);
                String teacherName = (teacher != null) ? teacher.getName() : "N/A";

                allSchedules.add(new Schedule(dateTimeString, session.getTitle(), teacherName, session.getClassroom()));
            }

            runOnUiThread(() -> {
                if (!allSchedules.isEmpty()) {
                    try {
                        String firstDateStr = allSchedules.get(0).getDatetime().split(" - ")[0];
                        currentCalendar.setTime(dateFormat.parse(firstDateStr));
                        selectedDay = currentCalendar.get(Calendar.DAY_OF_WEEK);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                updateWeekView();
            });
        });
    }

    private void updateWeekView() {
        updateDateRange();
        updateDayIndicators();
        updateDaySelection();
        updateScheduleList();
    }

    private void updateDateRange() {
        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_WEEK, tempCalendar.getFirstDayOfWeek());
        String startDate = dateFormat.format(tempCalendar.getTime());
        tempCalendar.add(Calendar.DAY_OF_YEAR, 6);
        String endDate = dateFormat.format(tempCalendar.getTime());
        binding.tvScheduleDateRange.setText(String.format("From %s - %s", startDate, endDate));
    }

    private void updateDayIndicators() {
        boolean[] hasScheduleInWeek = new boolean[8];
        Calendar weekStart = (Calendar) currentCalendar.clone();
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek());
        weekStart.set(Calendar.HOUR_OF_DAY, 0); weekStart.set(Calendar.MINUTE, 0); weekStart.set(Calendar.SECOND, 0);

        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.WEEK_OF_YEAR, 1);

        for (Schedule schedule : allSchedules) {
            try {
                Date scheduleDate = dateFormat.parse(schedule.getDatetime().split(" - ")[0]);
                if (!scheduleDate.before(weekStart.getTime()) && scheduleDate.before(weekEnd.getTime())) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(scheduleDate);
                    hasScheduleInWeek[cal.get(Calendar.DAY_OF_WEEK)] = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        binding.dayMonday.dotIndicator.setVisibility(hasScheduleInWeek[Calendar.MONDAY] ? View.VISIBLE : View.INVISIBLE);
        binding.dayTuesday.dotIndicator.setVisibility(hasScheduleInWeek[Calendar.TUESDAY] ? View.VISIBLE : View.INVISIBLE);
        binding.dayWednesday.dotIndicator.setVisibility(hasScheduleInWeek[Calendar.WEDNESDAY] ? View.VISIBLE : View.INVISIBLE);
        binding.dayThursday.dotIndicator.setVisibility(hasScheduleInWeek[Calendar.THURSDAY] ? View.VISIBLE : View.INVISIBLE);
        binding.dayFriday.dotIndicator.setVisibility(hasScheduleInWeek[Calendar.FRIDAY] ? View.VISIBLE : View.INVISIBLE);
        binding.daySaturday.dotIndicator.setVisibility(hasScheduleInWeek[Calendar.SATURDAY] ? View.VISIBLE : View.INVISIBLE);
        binding.daySunday.dotIndicator.setVisibility(hasScheduleInWeek[Calendar.SUNDAY] ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateDaySelection() {
        updateDayView(binding.dayMonday, "Mon", selectedDay == Calendar.MONDAY);
        updateDayView(binding.dayTuesday, "Tue", selectedDay == Calendar.TUESDAY);
        updateDayView(binding.dayWednesday, "Wed", selectedDay == Calendar.WEDNESDAY);
        updateDayView(binding.dayThursday, "Thu", selectedDay == Calendar.THURSDAY);
        updateDayView(binding.dayFriday, "Fri", selectedDay == Calendar.FRIDAY);
        updateDayView(binding.daySaturday, "Sat", selectedDay == Calendar.SATURDAY);
        updateDayView(binding.daySunday, "Sun", selectedDay == Calendar.SUNDAY);
    }

    private void updateDayView(ViewDayOfWeekBinding dayBinding, String dayName, boolean isSelected) {
        dayBinding.dayName.setText(dayName);
        int color = isSelected ? ContextCompat.getColor(this, R.color.purple_500) : ContextCompat.getColor(this, android.R.color.darker_gray);
        dayBinding.dayName.setTextColor(color);
    }

    private void updateScheduleList() {
        if (selectedDay == -1) {
            binding.recyclerViewSchedule.setVisibility(View.GONE);
            binding.tvNoSchedule.setVisibility(View.VISIBLE);
            return;
        }

        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_WEEK, selectedDay);
        String selectedDateStr = dateFormat.format(tempCalendar.getTime());

        List<Schedule> filteredSchedules = allSchedules.stream()
                .filter(s -> s.getDatetime().startsWith(selectedDateStr))
                .collect(Collectors.toList());

        if (filteredSchedules.isEmpty()) {
            binding.recyclerViewSchedule.setVisibility(View.GONE);
            binding.tvNoSchedule.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewSchedule.setVisibility(View.VISIBLE);
            binding.tvNoSchedule.setVisibility(View.GONE);
            ScheduleAdapter adapter = new ScheduleAdapter(filteredSchedules);
            binding.recyclerViewSchedule.setAdapter(adapter);
        }
    }
}
