package com.starlms.starlms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.TeacherAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityMessagesBinding;
import com.starlms.starlms.model.TeacherWithCourse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagesActivity extends AppCompatActivity implements TeacherAdapter.OnTeacherClickListener {

    private ActivityMessagesBinding binding;
    private TeacherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Tin nhắn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        loadTeachers();
    }

    private void setupRecyclerView() {
        binding.recyclerViewTeachers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadTeachers() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<TeacherWithCourse> teachersWithCourses = db.teacherDao().getTeachersWithCourses();

            runOnUiThread(() -> {
                adapter = new TeacherAdapter(teachersWithCourses, this);
                binding.recyclerViewTeachers.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onTeacherClick(TeacherWithCourse teacherWithCourse) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_TEACHER_ID, teacherWithCourse.getTeacher().getTeacherId());
        intent.putExtra(ChatActivity.EXTRA_TEACHER_NAME, teacherWithCourse.getTeacher().getName());
        startActivity(intent);
    }
}
