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
import com.starlms.starlms.databinding.FragmentProfileContactBinding;
import com.starlms.starlms.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileContactFragment extends Fragment {

    private FragmentProfileContactBinding binding;
    private AppDatabase db;
    private ExecutorService executor;
    private User currentUser;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileContactBinding.inflate(inflater, container, false);
        db = AppDatabase.getDatabase(getContext());
        executor = Executors.newSingleThreadExecutor();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadContactData();

        binding.buttonEditContact.setOnClickListener(v -> toggleEditMode());
        binding.buttonSaveContact.setOnClickListener(v -> saveContactData());
    }

    private void loadContactData() {
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
        binding.textContactFullName.setText(user.getContactName());
        binding.textContactPhone.setText(user.getContactPhone());

        // Pre-fill EditTexts for editing
        binding.editTextContactFullName.setText(user.getContactName());
        binding.editTextContactPhone.setText(user.getContactPhone());
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;

        // Toggle visibility for TextViews
        binding.textContactFullName.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        binding.textContactPhone.setVisibility(isEditMode ? View.GONE : View.VISIBLE);

        // Toggle visibility for EditTexts
        binding.inputLayoutContactFullName.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        binding.inputLayoutContactPhone.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        // Toggle button visibility
        binding.buttonEditContact.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        binding.buttonSaveContact.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    private void saveContactData() {
        currentUser.setContactName(binding.editTextContactFullName.getText().toString());
        currentUser.setContactPhone(binding.editTextContactPhone.getText().toString());

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
