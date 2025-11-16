package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.starlms.starlms.entity.UserSurveyCompletion;

@Dao
public interface UserSurveyCompletionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserSurveyCompletion completion);

    @Query("SELECT * FROM user_survey_completions WHERE user_id = :userId AND survey_id = :surveyId LIMIT 1")
    UserSurveyCompletion getCompletion(long userId, int surveyId);
}
