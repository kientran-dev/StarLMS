package com.starlms.starlms.admin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.starlms.starlms.R;
import com.starlms.starlms.adapter.AdminLeaveRequestAdapter;
import com.starlms.starlms.dao.LeaveRequestDao;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.entity.LeaveRequest;
import com.starlms.starlms.model.LeaveRequestWithUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminLeaveListFragment extends Fragment implements AdminLeaveRequestAdapter.OnActionClickListener {

    private static final String ARG_COURSE_ID = "course_id";
    private static final String ARG_COURSE_NAME = "course_name";

    private long courseId;
    private String courseName;

    private LeaveRequestDao leaveRequestDao;
    private RecyclerView recyclerView;
    private AdminLeaveRequestAdapter adapter;
    private ExecutorService executorService;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public static AdminLeaveListFragment newInstance(long courseId, String courseName) {
        AdminLeaveListFragment fragment = new AdminLeaveListFragment();
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
        leaveRequestDao = db.leaveRequestDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_leave_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_leave_list);
        toolbar.setTitle(courseName != null ? "Đơn nghỉ - " + courseName : "Đơn nghỉ");
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_view_leave_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdminLeaveRequestAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnActionClickListener(this);

        loadLeaveRequests();
    }

    private void loadLeaveRequests() {
        executorService.execute(() -> {
            List<LeaveRequestWithUser> requests = leaveRequestDao.getLeaveRequestsForCourse(courseId);
            mainThreadHandler.post(() -> adapter.setRequests(requests));
        });
    }

    private void updateRequestStatus(LeaveRequestWithUser request, String status) {
        executorService.execute(() -> {
            LeaveRequest leaveRequest = request.getLeaveRequest();
            leaveRequest.setStatus(status);
            leaveRequestDao.update(leaveRequest);
            mainThreadHandler.post(() -> {
                Toast.makeText(getContext(), "Đã cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                loadLeaveRequests(); // Refresh the list
            });
        });
    }

    @Override
    public void onApproveClick(LeaveRequestWithUser request) {
        // SỬA Ở ĐÂY: Dùng chuỗi tiếng Việt
        updateRequestStatus(request, "Đã duyệt");
    }

    @Override
    public void onRejectClick(LeaveRequestWithUser request) {
        // SỬA Ở ĐÂY: Dùng chuỗi tiếng Việt
        updateRequestStatus(request, "Bị từ chối");
    }
}
