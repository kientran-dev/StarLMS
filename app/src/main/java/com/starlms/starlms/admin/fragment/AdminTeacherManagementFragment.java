package com.starlms.starlms.admin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.starlms.starlms.adapter.AdminTeacherAdapter;
import com.starlms.starlms.dao.TeacherDao;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.entity.Teacher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminTeacherManagementFragment extends Fragment {

    private TeacherDao teacherDao;
    private RecyclerView recyclerView;
    private AdminTeacherAdapter adapter;
    private ExecutorService executorService;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        teacherDao = db.teacherDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_teacher_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_teacher_management);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        recyclerView = view.findViewById(R.id.recycler_view_teachers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdminTeacherAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemInteractionListener(this::showOptionsDialog);

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_teacher);
        fabAdd.setOnClickListener(v -> showTeacherDialog(null));

        loadTeachers();
    }

    private void loadTeachers() {
        executorService.execute(() -> {
            List<Teacher> teachers = teacherDao.getAll();
            mainThreadHandler.post(() -> adapter.setTeachers(teachers));
        });
    }

    private void showOptionsDialog(final Teacher teacher) {
        final CharSequence[] options = {"Cập nhật", "Xóa"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Lựa chọn cho: " + teacher.getName())
                .setItems(options, (dialog, item) -> {
                    if (options[item].equals("Cập nhật")) {
                        showTeacherDialog(teacher);
                    } else if (options[item].equals("Xóa")) {
                        showDeleteConfirmationDialog(teacher);
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog(final Teacher teacher) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa giảng viên '" + teacher.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteTeacher(teacher))
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void deleteTeacher(final Teacher teacher) {
        executorService.execute(() -> {
            teacherDao.delete(teacher);
            mainThreadHandler.post(() -> {
                Toast.makeText(getContext(), "Đã xóa giảng viên", Toast.LENGTH_SHORT).show();
                loadTeachers();
            });
        });
    }

    private void showTeacherDialog(@Nullable final Teacher teacher) {
        boolean isUpdate = (teacher != null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isUpdate ? "Cập Nhật Giảng Viên" : "Thêm Giảng Viên Mới");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.admin_dialog_add_update_teacher, null);
        final EditText etName = dialogView.findViewById(R.id.et_teacher_name);
        final EditText etEmail = dialogView.findViewById(R.id.et_teacher_email);
        final EditText etPhone = dialogView.findViewById(R.id.et_teacher_phone);
        builder.setView(dialogView);

        if (isUpdate) {
            etName.setText(teacher.getName());
            etEmail.setText(teacher.getEmail());
            etPhone.setText(teacher.getPhoneNumber());
        }

        builder.setPositiveButton(isUpdate ? "Cập Nhật" : "Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                Teacher existingEmail = teacherDao.findByEmail(email);
                Teacher existingPhone = teacherDao.findByPhoneNumber(phone);

                boolean isEmailTaken = false;
                boolean isPhoneTaken = false;

                if (isUpdate) {
                    if (existingEmail != null && existingEmail.getTeacherId() != teacher.getTeacherId()) {
                        isEmailTaken = true;
                    }
                    if (existingPhone != null && existingPhone.getTeacherId() != teacher.getTeacherId()) {
                        isPhoneTaken = true;
                    }
                } else {
                    if (existingEmail != null) {
                        isEmailTaken = true;
                    }
                    if (existingPhone != null) {
                        isPhoneTaken = true;
                    }
                }

                if (isEmailTaken || isPhoneTaken) {
                    String message = isEmailTaken ? "Email đã được sử dụng." : "Số điện thoại đã được sử dụng.";
                    mainThreadHandler.post(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
                } else {
                    if (isUpdate) {
                        teacher.setName(name);
                        teacher.setEmail(email);
                        teacher.setPhoneNumber(phone);
                        teacherDao.update(teacher);
                    } else {
                        Teacher newTeacher = new Teacher(name, email, phone);
                        teacherDao.insert(newTeacher);
                    }
                    mainThreadHandler.post(() -> {
                        Toast.makeText(getContext(), isUpdate ? "Đã cập nhật" : "Đã thêm", Toast.LENGTH_SHORT).show();
                        loadTeachers();
                    });
                }
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
