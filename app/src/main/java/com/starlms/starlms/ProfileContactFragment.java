package com.starlms.starlms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileContactBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadContactData();
    }

    private void loadContactData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getContext());
            // For simplicity, we're getting the first user.
            // In a real app, you'd pass the user ID.
            User user = db.userDao().getAll().get(0);

            getActivity().runOnUiThread(() -> {
                if (user != null) {
                    binding.contactFullName.setText(user.getContactName());
                    binding.contactPhone.setText(user.getContactPhone());
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
