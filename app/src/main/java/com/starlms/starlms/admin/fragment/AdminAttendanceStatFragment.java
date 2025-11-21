package com.starlms.starlms.admin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.starlms.starlms.R;
import com.starlms.starlms.adapter.AdminAttendanceStatAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.entity.Attendance;
import com.starlms.starlms.entity.LeaveRequest;
import com.starlms.starlms.entity.Session;
import com.starlms.starlms.entity.User;
import com.starlms.starlms.model.AttendanceStat;
import com.starlms.starlms.model.LeaveRequestWithUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AdminAttendanceStatFragment extends Fragment {

    private static final String ARG_COURSE_ID = "course_id";
    private static final String ARG_COURSE_NAME = "course_name";

    private long courseId;
    private String courseName;

    private AppDatabase db;
    private RecyclerView recyclerView;
    private AdminAttendanceStatAdapter adapter;
    private ExecutorService executorService;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public static AdminAttendanceStatFragment newInstance(long courseId, String courseName) {
        AdminAttendanceStatFragment fragment = new AdminAttendanceStatFragment();
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
        db = AppDatabase.getDatabase(requireContext());
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_attendance_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_attendance_stats);
        toolbar.setTitle(courseName != null ? "Chuyên cần - " + courseName : "Thống kê Chuyên cần");
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_view_attendance_stats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdminAttendanceStatAdapter();
        recyclerView.setAdapter(adapter);

        loadStats();
    }

    private void loadStats() {
        executorService.execute(() -> {
            // 1. Get all base data
            List<User> enrolledUsers = db.courseDao().getUsersByCourse(courseId);
            List<Session> allCourseSessions = db.sessionDao().getSessionsForCourse((int) courseId);
            List<Attendance> allAttendanceRecords = db.attendanceDao().getAttendanceForCourse(courseId);
            List<LeaveRequestWithUser> allLeaveRequests = db.leaveRequestDao().getLeaveRequestsForCourse(courseId);

            long now = System.currentTimeMillis();

            // 2. Filter for past sessions only
            List<Session> pastSessions = allCourseSessions.stream()
                    .filter(s -> s.getSessionDate() > 0 && s.getSessionDate() < now)
                    .collect(Collectors.toList());

            // 3. Create a lookup for approved leave request reasons by userId
            Map<Long, Set<String>> approvedLeaveReasonsByUser = allLeaveRequests.stream()
                .filter(lr -> "Đã duyệt".equalsIgnoreCase(lr.getLeaveRequest().getStatus()))
                .collect(Collectors.groupingBy(
                    lr -> (long) lr.getUser().getId(),
                    Collectors.mapping(lr -> lr.getLeaveRequest().getReason(), Collectors.toSet())
                ));

            // 4. Group all attendance records by userId for quick lookup
            Map<Long, List<Attendance>> attendanceByUser = allAttendanceRecords.stream()
                    .collect(Collectors.groupingBy(att -> (long)att.getUserId()));

            // 5. Calculate stats for each user
            List<AttendanceStat> finalStats = new ArrayList<>();
            for (User user : enrolledUsers) {
                AttendanceStat userStat = new AttendanceStat(user);
                int presentCount = 0;
                int approvedLeaveCount = 0;

                // Get this user's approved reasons and their attendance records
                // SỬA Ở ĐÂY: Ép kiểu user.getId() thành (long)
                Set<String> userApprovedReasons = approvedLeaveReasonsByUser.getOrDefault((long) user.getId(), Collections.emptySet());
                List<Attendance> userAttendanceRecords = attendanceByUser.getOrDefault((long) user.getId(), Collections.emptyList());
                
                Map<Integer, Attendance> userAttendanceMap = userAttendanceRecords.stream()
                        .collect(Collectors.toMap(Attendance::getSessionId, att -> att, (att1, att2) -> att1));

                for (Session session : pastSessions) {
                    Attendance attendance = userAttendanceMap.get(session.getSessionId());
                    if (attendance != null) {
                        if ("PRESENT".equalsIgnoreCase(attendance.getStatus())) {
                            presentCount++;
                        } else if ("LEAVE_REQUESTED".equalsIgnoreCase(attendance.getStatus())) {
                            if (userApprovedReasons.contains(attendance.getReason())) {
                                approvedLeaveCount++;
                            }
                        }
                    }
                }
                
                userStat.setTotalSessions(pastSessions.size());
                userStat.setPresentCount(presentCount);
                userStat.setLeaveCount(approvedLeaveCount);
                finalStats.add(userStat);
            }

            mainThreadHandler.post(() -> adapter.setStats(finalStats));
        });
    }
}
