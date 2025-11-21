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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.starlms.starlms.R;
import com.starlms.starlms.adapter.AdminCourseAdapter;
import com.starlms.starlms.dao.CourseDao;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.model.CourseWithTeacher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminAttendanceCourseSelectionFragment extends Fragment {

    private CourseDao courseDao;
    private RecyclerView recyclerView;
    private AdminCourseAdapter adapter;
    private ExecutorService executorService;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        courseDao = db.courseDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_attendance_course_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_attendance_course_selection);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_view_attendance_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdminCourseAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemInteractionListener(new AdminCourseAdapter.OnItemInteractionListener() {
            @Override
            public void onItemClick(CourseWithTeacher courseWithTeacher) {
                // SỬA Ở ĐÂY: Điều hướng đến màn hình thống kê
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, AdminAttendanceStatFragment.newInstance(
                        courseWithTeacher.getCourse().getCourseId(),
                        courseWithTeacher.getCourse().getName()
                ));
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onItemLongClick(CourseWithTeacher courseWithTeacher) {
                // No action needed
            }
        });

        loadCourses();
    }

    private void loadCourses() {
        executorService.execute(() -> {
            List<CourseWithTeacher> courses = courseDao.getCoursesWithTeachers();
            mainThreadHandler.post(() -> adapter.setCourses(courses));
        });
    }
}
