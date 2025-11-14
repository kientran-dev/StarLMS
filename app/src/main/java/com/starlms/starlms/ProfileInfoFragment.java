package com.starlms.starlms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.FragmentProfileInfoBinding;
import com.starlms.starlms.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileInfoFragment extends Fragment {

    private FragmentProfileInfoBinding binding;
    private AppDatabase db;
    private ExecutorService executor;
    private User currentUser;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileInfoBinding.inflate(inflater, container, false);
        db = AppDatabase.getDatabase(getContext());
        executor = Executors.newSingleThreadExecutor();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserData();

        binding.buttonEdit.setOnClickListener(v -> toggleEditMode());
        binding.buttonSave.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        executor.execute(() -> {
            // Assuming user ID 1 for simplicity
            currentUser = db.userDao().findById(1);
            getActivity().runOnUiThread(() -> {
                if (currentUser != null) {
                    populateUI(currentUser);
                }
            });
        });
    }

    private void populateUI(User user) {
        // Set text for TextViews
        binding.textStudentId.setText(user.getStudentId());
        binding.textFullName.setText(user.getFullName());
        binding.textDob.setText(user.getDateOfBirth());
        binding.textGender.setText(user.getGender());
        binding.textPhone.setText(user.getPhone());
        binding.textAddress.setText(user.getAddress());

        // Pre-fill EditTexts for editing
        binding.editTextFullName.setText(user.getFullName());
        binding.editTextDob.setText(user.getDateOfBirth());
        binding.editTextGender.setText(user.getGender());
        binding.editTextPhone.setText(user.getPhone());
        binding.editTextAddress.setText(user.getAddress());
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;

        // Toggle visibility for TextViews
        binding.textFullName.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        binding.textDob.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        binding.textGender.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        binding.textPhone.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        binding.textAddress.setVisibility(isEditMode ? View.GONE : View.VISIBLE);

        // Toggle visibility for EditTexts
        binding.inputLayoutFullName.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        binding.inputLayoutDob.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        binding.inputLayoutGender.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        binding.inputLayoutPhone.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        binding.inputLayoutAddress.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        // Toggle button visibility
        binding.buttonEdit.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        binding.buttonSave.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    private void saveUserData() {
        // Update the user object with new data from EditTexts
        currentUser.setFullName(binding.editTextFullName.getText().toString());
        currentUser.setDateOfBirth(binding.editTextDob.getText().toString());
        currentUser.setGender(binding.editTextGender.getText().toString());
        currentUser.setPhone(binding.editTextPhone.getText().toString());
        currentUser.setAddress(binding.editTextAddress.getText().toString());

        executor.execute(() -> {
            db.userDao().update(currentUser);
            getActivity().runOnUiThread(() -> {
                populateUI(currentUser);
                toggleEditMode(); // Switch back to view mode
                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
