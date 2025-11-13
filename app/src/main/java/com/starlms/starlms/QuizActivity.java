package com.starlms.starlms;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.databinding.ActivityQuizBinding;
import com.starlms.starlms.entity.Grade;
import com.starlms.starlms.entity.Question;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_TEST_ID = "EXTRA_TEST_ID";

    private ActivityQuizBinding binding;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int testId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        testId = getIntent().getIntExtra(EXTRA_TEST_ID, -1);
        if (testId == -1) {
            finish(); // Cannot proceed without a test ID
            return;
        }

        setSupportActionBar(binding.toolbar);

        loadQuizData();

        binding.nextButton.setOnClickListener(v -> handleNextButtonClick());
    }

    private void loadQuizData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            questions = db.questionDao().getQuestionsForTest(testId);
            userId = db.userDao().getAll().get(0).getId(); // Assuming single user

            runOnUiThread(() -> {
                if (questions != null && !questions.isEmpty()) {
                    displayQuestion();
                } else {
                    Toast.makeText(this, "Không có câu hỏi cho bài tập này.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void displayQuestion() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        binding.questionText.setText((currentQuestionIndex + 1) + ". " + currentQuestion.getQuestionText());
        binding.optionA.setText(currentQuestion.getOptionA());
        binding.optionB.setText(currentQuestion.getOptionB());
        binding.optionC.setText(currentQuestion.getOptionC());
        binding.optionD.setText(currentQuestion.getOptionD());
        binding.optionsGroup.clearCheck();

        if (currentQuestionIndex == questions.size() - 1) {
            binding.nextButton.setText("Nộp bài");
        }
    }

    private void handleNextButtonClick() {
        int selectedOptionId = binding.optionsGroup.getCheckedRadioButtonId();
        if (selectedOptionId == -1) {
            Toast.makeText(this, "Vui lòng chọn một đáp án", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedOptionId);
        int selectedAnswer = binding.optionsGroup.indexOfChild(selectedRadioButton) + 1;

        if (selectedAnswer == questions.get(currentQuestionIndex).getCorrectOption()) {
            score++;
        }

        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion();
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            Grade newGrade = new Grade(0, score, userId, testId);
            db.gradeDao().insertAll(newGrade);

            runOnUiThread(() -> {
                Toast.makeText(this, "Bạn đã hoàn thành bài thi! Điểm của bạn: " + score + "/" + questions.size(), Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }
}
