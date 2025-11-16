package com.starlms.starlms.admin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

        // Setup Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_course_management);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_view_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdminCourseAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemInteractionListener(this::showOptionsDialog);

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
        final Spinner spinnerType = dialogView.findViewById(R.id.spinner_course_type);
        final Spinner spinnerTeacher = dialogView.findViewById(R.id.spinner_course_teacher);
        builder.setView(dialogView);

        // Setup Spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.course_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        List<String> teacherNames = teacherList.stream().map(Teacher::getName).collect(Collectors.toList());
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, teacherNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);

        if (isUpdate) {
            Course course = courseWithTeacher.getCourse();
            etName.setText(course.getName());
            spinnerType.setSelection(course.getType().equalsIgnoreCase("online") ? 0 : 1);

            // Find and set selected teacher
            for (int i = 0; i < teacherList.size(); i++) {
                if (teacherList.get(i).getTeacherId() == course.getTeacherId()) {
                    spinnerTeacher.setSelection(i);
                    break;
                }
            }
        }

        builder.setPositiveButton(isUpdate ? "Cập Nhật" : "Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            int selectedTeacherPosition = spinnerTeacher.getSelectedItemPosition();

            if (TextUtils.isEmpty(name) || teacherList.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên và đảm bảo có giảng viên", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher selectedTeacher = teacherList.get(selectedTeacherPosition);

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
