package com.starlms.starlms.admin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.starlms.starlms.adapter.AdminCourseAdapter;
import com.starlms.starlms.dao.CourseDao;
import com.starlms.starlms.dao.TeacherDao;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.entity.Course;
import com.starlms.starlms.entity.Teacher;
import com.starlms.starlms.model.CourseWithTeacher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AdminCourseManagementFragment extends Fragment {

    private CourseDao courseDao;
    private TeacherDao teacherDao;
    private RecyclerView recyclerView;
    private AdminCourseAdapter adapter;
    private ExecutorService executorService;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private List<Teacher> teacherList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        courseDao = db.courseDao();
        teacherDao = db.teacherDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_course_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_course_management);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_view_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdminCourseAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemInteractionListener(new AdminCourseAdapter.OnItemInteractionListener() {
            @Override
            public void onItemClick(CourseWithTeacher courseWithTeacher) {
                // No action
            }

            @Override
            public void onItemLongClick(CourseWithTeacher courseWithTeacher) {
                showOptionsDialog(courseWithTeacher);
            }
        });

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_course);
        fabAdd.setOnClickListener(v -> showCourseDialog(null));

        loadInitialData();
    }

    private void loadInitialData() {
        executorService.execute(() -> {
            teacherList = teacherDao.getAll();
            List<CourseWithTeacher> courses = courseDao.getCoursesWithTeachers();
            mainThreadHandler.post(() -> adapter.setCourses(courses));
        });
    }

    private void showOptionsDialog(final CourseWithTeacher courseWithTeacher) {
        final CharSequence[] options = {"Cập nhật", "Xóa"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Lựa chọn cho: " + courseWithTeacher.getCourse().getName())
                .setItems(options, (dialog, item) -> {
                    if (options[item].equals("Cập nhật")) {
                        showCourseDialog(courseWithTeacher);
                    } else if (options[item].equals("Xóa")) {
                        showDeleteConfirmationDialog(courseWithTeacher.getCourse());
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog(final Course course) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa khóa học '" + course.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCourse(course))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCourse(final Course course) {
        executorService.execute(() -> {
            courseDao.delete(course);
            mainThreadHandler.post(() -> {
                Toast.makeText(getContext(), "Đã xóa khóa học", Toast.LENGTH_SHORT).show();
                loadInitialData();
            });
        });
    }

    private void showCourseDialog(@Nullable final CourseWithTeacher courseWithTeacher) {
        boolean isUpdate = (courseWithTeacher != null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isUpdate ? "Cập Nhật Khóa Học" : "Thêm Khóa Học Mới");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.admin_dialog_add_update_course, null);
        final EditText etName = dialogView.findViewById(R.id.et_course_name);
        final AutoCompleteTextView spinnerType = dialogView.findViewById(R.id.spinner_course_type);
        final AutoCompleteTextView spinnerTeacher = dialogView.findViewById(R.id.spinner_course_teacher);
        builder.setView(dialogView);

        // Setup Dropdowns
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.course_types, android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        List<String> teacherNames = teacherList.stream().map(Teacher::getName).collect(Collectors.toList());
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, teacherNames);
        spinnerTeacher.setAdapter(teacherAdapter);

        if (isUpdate) {
            Course course = courseWithTeacher.getCourse();
            etName.setText(course.getName());
            spinnerType.setText(course.getType(), false);
            if (courseWithTeacher.getTeacher() != null) {
                spinnerTeacher.setText(courseWithTeacher.getTeacher().getName(), false);
            }
        }

        builder.setPositiveButton(isUpdate ? "Cập Nhật" : "Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String type = spinnerType.getText().toString();
            String selectedTeacherName = spinnerTeacher.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(type) || TextUtils.isEmpty(selectedTeacherName)) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher selectedTeacher = teacherList.stream()
                    .filter(t -> t.getName().equals(selectedTeacherName))
                    .findFirst()
                    .orElse(null);

            if (selectedTeacher == null) {
                Toast.makeText(getContext(), "Giảng viên không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                if (isUpdate) {
                    Course courseToUpdate = courseWithTeacher.getCourse();
                    courseToUpdate.setName(name);
                    courseToUpdate.setType(type);
                    courseToUpdate.setTeacherId(selectedTeacher.getTeacherId());
                    courseDao.update(courseToUpdate);
                } else {
                    Course newCourse = new Course(0, name, type, selectedTeacher.getTeacherId());
                    courseDao.insert(newCourse);
                }
                mainThreadHandler.post(() -> {
                    Toast.makeText(getContext(), isUpdate ? "Đã cập nhật" : "Đã thêm", Toast.LENGTH_SHORT).show();
                    loadInitialData();
                });
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
