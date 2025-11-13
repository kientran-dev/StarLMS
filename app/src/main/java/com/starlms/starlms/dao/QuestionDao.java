package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.starlms.starlms.entity.Question;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert
    void insertAll(Question... questions);

    @Query("SELECT * FROM questions WHERE testOwnerId = :testId")
    List<Question> getQuestionsForTest(int testId);
}
