package com.starlms.starlms.admin.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.starlms.starlms.R;
import com.starlms.starlms.adapter.StudentAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.entity.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminStudentManagementFragment extends Fragment implements StudentAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<User> studentList = new ArrayList<>();
    private AppDatabase db;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Calendar myCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_student_management, container, false);

        db = AppDatabase.getDatabase(requireContext());

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_student_management);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_view_students);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudentAdapter(getContext(), studentList, this);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.fab_add_student).setOnClickListener(v -> {
            showAddOrUpdateStudentDialog(null);
        });

        loadStudents();

        return view;
    }

    private void loadStudents() {
        executorService.execute(() -> {
            List<User> users = db.userDao().getAll();
            requireActivity().runOnUiThread(() -> {
                studentList.clear();
                studentList.addAll(users);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void showAddOrUpdateStudentDialog(final User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.admin_dialog_add_student, null);

        final TextInputEditText etUsername = dialogView.findViewById(R.id.et_username);
        final TextInputEditText etPassword = dialogView.findViewById(R.id.et_password);
        final TextInputEditText etStudentId = dialogView.findViewById(R.id.et_student_id);
        final TextInputEditText etFullName = dialogView.findViewById(R.id.et_full_name);
        final TextInputEditText etEmail = dialogView.findViewById(R.id.et_email);
        final TextInputEditText etDob = dialogView.findViewById(R.id.et_dob);
        final RadioGroup rgGender = dialogView.findViewById(R.id.rg_gender);
        final RadioButton rbMale = dialogView.findViewById(R.id.rb_male);
        final RadioButton rbFemale = dialogView.findViewById(R.id.rb_female);
        final TextInputEditText etPhone = dialogView.findViewById(R.id.et_phone);
        final TextInputEditText etAddress = dialogView.findViewById(R.id.et_address);
        final TextInputEditText etContactName = dialogView.findViewById(R.id.et_contact_name);
        final TextInputEditText etContactPhone = dialogView.findViewById(R.id.et_contact_phone);

        // --- Date Picker Logic ---
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(etDob);
        };
        etDob.setOnClickListener(v -> new DatePickerDialog(getContext(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        // -------------------------

        if (user != null) {
            builder.setTitle("Cập nhật học viên");
            etUsername.setText(user.getUsername());
            etPassword.setText(user.getPassword());
            etStudentId.setText(user.getStudentId());
            etFullName.setText(user.getFullName());
            etEmail.setText(user.getEmail());
            etDob.setText(user.getDateOfBirth());
            if (user.getGender() != null) {
                if (user.getGender().equals("Nam")) {
                    rbMale.setChecked(true);
                } else if (user.getGender().equals("Nữ")) {
                    rbFemale.setChecked(true);
                }
            }
            etPhone.setText(user.getPhone());
            etAddress.setText(user.getAddress());
            etContactName.setText(user.getContactName());
            etContactPhone.setText(user.getContactPhone());
        } else {
            builder.setTitle("Thêm học viên");
        }

        builder.setView(dialogView)
                .setPositiveButton(user != null ? "Cập nhật" : "Thêm", (dialog, id) -> {
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String studentId = etStudentId.getText().toString().trim();
                    String fullName = etFullName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String dob = etDob.getText().toString().trim();
                    int selectedGenderId = rgGender.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = dialogView.findViewById(selectedGenderId);
                    String gender = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "";
                    String phone = etPhone.getText().toString().trim();
                    String address = etAddress.getText().toString().trim();
                    String contactName = etContactName.getText().toString().trim();
                    String contactPhone = etContactPhone.getText().toString().trim();

                    if (username.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (user != null) {
                        // Update existing user
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setStudentId(studentId);
                        user.setFullName(fullName);
                        user.setEmail(email);
                        user.setDateOfBirth(dob);
                        user.setGender(gender);
                        user.setPhone(phone);
                        user.setAddress(address);
                        user.setContactName(contactName);
                        user.setContactPhone(contactPhone);
                        updateUser(user);
                    } else {
                        // Add new user
                        User newUser = new User();
                        newUser.setUsername(username);
                        newUser.setPassword(password);
                        newUser.setEmail(email);
                        newUser.setStudentId(studentId);
                        newUser.setFullName(fullName);
                        newUser.setDateOfBirth(dob);
                        newUser.setGender(gender);
                        newUser.setPhone(phone);
                        newUser.setAddress(address);
                        newUser.setContactName(contactName);
                        newUser.setContactPhone(contactPhone);
                        insertUser(newUser);
                    }
                })
                .setNegativeButton("Hủy", (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    private void updateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy"; 
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    private void insertUser(User user) {
        executorService.execute(() -> {
            db.userDao().insertAndGetId(user);
            loadStudents();
        });
    }

    private void updateUser(User user) {
        executorService.execute(() -> {
            db.userDao().update(user);
            loadStudents();
        });
    }

    private void deleteUser(User user) {
        executorService.execute(() -> {
            db.userDao().delete(user);
            loadStudents();
        });
    }

    @Override
    public void onItemClick(User user) {
        final CharSequence[] options = {"Cập nhật", "Xóa"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(user.getFullName());
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Cập nhật")) {
                showAddOrUpdateStudentDialog(user);
            } else if (options[item].equals("Xóa")) {
                showDeleteConfirmationDialog(user);
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa học viên này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Hủy", null)
                .show();
    }
}
