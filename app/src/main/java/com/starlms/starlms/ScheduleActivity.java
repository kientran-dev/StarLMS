package com.starlms.starlms;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.ScheduleAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityScheduleBinding;
import com.starlms.starlms.databinding.ViewDayOfWeekBinding;
import com.starlms.starlms.entity.Schedule;
import com.starlms.starlms.entity.Session;
import com.starlms.starlms.entity.SessionDetails;
import com.starlms.starlms.entity.Teacher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ScheduleActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "COURSE_ID";

    private ActivityScheduleBinding binding;
    private Calendar currentWeekCalendar;
    private Calendar selectedDateCalendar;
    private SimpleDateFormat dayOfWeekFormat;
    private SimpleDateFormat dayOfMonthFormat;
    private List<Schedule> allSchedules;
    private Set<Integer> daysWithSchedulesInWeek;
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
            getSupportActionBar().setTitle(courseName != null ? "Thời khóa biểu: " + courseName : "Thời khóa biểu");
        }

        currentWeekCalendar = Calendar.getInstance();
        selectedDateCalendar = Calendar.getInstance();
        dayOfWeekFormat = new SimpleDateFormat("EEE", Locale.forLanguageTag("vi-VN"));
        dayOfMonthFormat = new SimpleDateFormat("d", Locale.getDefault());

        allSchedules = new ArrayList<>();
        daysWithSchedulesInWeek = new HashSet<>();

        setupRecyclerView();
        setupWeekNavigation();
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
            currentWeekCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            updateWeekView();
        });

        binding.btnNextWeek.setOnClickListener(v -> {
            currentWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            updateWeekView();
        });
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
                String teacherInfo = (teacher != null) ? teacher.getName() + " (" + teacher.getPhoneNumber() + ")" : "N/A";

                allSchedules.add(new Schedule(
                        session.getSessionId(),
                        session.getTitle(),
                        new Date(session.getSessionDate()),
                        teacherInfo,
                        session.getClassroom()
                ));
            }

            runOnUiThread(() -> updateWeekView());
        });
    }

    private void updateWeekView() {
        updateDaysWithSchedules();

        Calendar tempCal = (Calendar) currentWeekCalendar.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        ViewDayOfWeekBinding[] dayBindings = {binding.dayMonday, binding.dayTuesday, binding.dayWednesday, binding.dayThursday, binding.dayFriday, binding.daySaturday, binding.daySunday};

        for (ViewDayOfWeekBinding dayBinding : dayBindings) {
            final Calendar dayCal = (Calendar) tempCal.clone();
            updateDayView(dayBinding, dayCal);
            dayBinding.getRoot().setOnClickListener(v -> {
                selectedDateCalendar = dayCal;
                updateWeekView();
            });
            tempCal.add(Calendar.DAY_OF_YEAR, 1);
        }
        updateScheduleList();
    }

    private void updateDaysWithSchedules() {
        daysWithSchedulesInWeek.clear();
        Calendar weekStart = (Calendar) currentWeekCalendar.clone();
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekStart.set(Calendar.HOUR_OF_DAY, 0); // Start of the day

        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.WEEK_OF_YEAR, 1);

        for (Schedule schedule : allSchedules) {
            if (!schedule.getDateTime().before(weekStart.getTime()) && schedule.getDateTime().before(weekEnd.getTime())) {
                Calendar scheduleCal = Calendar.getInstance();
                scheduleCal.setTime(schedule.getDateTime());
                daysWithSchedulesInWeek.add(scheduleCal.get(Calendar.DAY_OF_YEAR));
            }
        }
    }

    private void updateDayView(ViewDayOfWeekBinding dayBinding, Calendar calendar) {
        dayBinding.dayName.setText(dayOfWeekFormat.format(calendar.getTime()).replace("Th ", "T"));
        dayBinding.dayDate.setText(dayOfMonthFormat.format(calendar.getTime()));

        boolean isSelected = isSameDay(calendar, selectedDateCalendar);
        dayBinding.dayDate.setSelected(isSelected);

        boolean hasSchedule = daysWithSchedulesInWeek.contains(calendar.get(Calendar.DAY_OF_YEAR));
        dayBinding.dotIndicator.setVisibility(hasSchedule ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateScheduleList() {
        List<Schedule> filteredSchedules = allSchedules.stream()
                .filter(schedule -> isSameDay(schedule.getDateTime(), selectedDateCalendar))
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

    private boolean isSameDay(Date date, Calendar cal) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        return isSameDay(dateCal, cal);
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
