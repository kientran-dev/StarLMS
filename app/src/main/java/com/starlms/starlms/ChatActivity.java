package com.starlms.starlms;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.starlms.starlms.adapter.ChatAdapter;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityChatBinding;
import com.starlms.starlms.entity.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_TEACHER_ID = "EXTRA_TEACHER_ID";
    public static final String EXTRA_TEACHER_NAME = "EXTRA_TEACHER_NAME";

    private ActivityChatBinding binding;
    private ChatAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private AppDatabase db;
    private ExecutorService executor;
    private long teacherId;
    private String teacherName;
    private long currentUserId = 1; // Assuming student user ID is 1

    private static final List<String> TEACHER_REPLIES = Arrays.asList(
            "Chào em, cô đã nhận được tin nhắn.",
            "OK em, cô sẽ xem xét và phản hồi sớm.",
            "Cảm ơn em đã thông báo.",
            "Em cần hỗ trợ thêm gì không?",
            "Đã nhận được. Cảm ơn em."
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(getApplicationContext());
        executor = Executors.newSingleThreadExecutor();

        teacherId = getIntent().getLongExtra(EXTRA_TEACHER_ID, -1);
        teacherName = getIntent().getStringExtra(EXTRA_TEACHER_NAME);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(teacherName != null ? teacherName : "Trò chuyện");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        loadMessages();

        binding.buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerViewChat.setLayoutManager(layoutManager);
        binding.recyclerViewChat.setAdapter(adapter);
    }

    private void loadMessages() {
        executor.execute(() -> {
            List<Message> loadedMessages = db.messageDao().getMessagesBetween(currentUserId, teacherId);
            runOnUiThread(() -> {
                messages.clear();
                messages.addAll(loadedMessages);
                adapter.notifyDataSetChanged();
                binding.recyclerViewChat.scrollToPosition(messages.size() - 1);
            });
        });
    }

    private void sendMessage() {
        String text = binding.editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        long timestamp = System.currentTimeMillis();
        Message newMessage = new Message(text, timestamp, currentUserId, teacherId, true);

        executor.execute(() -> {
            db.messageDao().insert(newMessage);
            runOnUiThread(() -> {
                messages.add(newMessage);
                adapter.notifyItemInserted(messages.size() - 1);
                binding.recyclerViewChat.scrollToPosition(messages.size() - 1);
                binding.editTextMessage.setText("");

                // Simulate a reply from the teacher
                simulateTeacherReply();
            });
        });
    }

    private void simulateTeacherReply() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            long timestamp = System.currentTimeMillis();
            Random random = new Random();
            String replyText = TEACHER_REPLIES.get(random.nextInt(TEACHER_REPLIES.size()));

            Message replyMessage = new Message(replyText, timestamp, teacherId, currentUserId, false);
            executor.execute(() -> {
                db.messageDao().insert(replyMessage);
                runOnUiThread(() -> {
                    messages.add(replyMessage);
                    adapter.notifyItemInserted(messages.size() - 1);
                    binding.recyclerViewChat.scrollToPosition(messages.size() - 1);
                });
            });
        }, 1500); // 1.5-second delay
    }
}
