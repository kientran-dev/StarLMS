package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.starlms.starlms.entity.SurveyResponse;
import com.starlms.starlms.model.SurveyResponseWithUser;

import java.util.List;

@Dao
public interface SurveyResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SurveyResponse response);

    @Transaction
    @Query("SELECT * FROM survey_responses WHERE survey_id = :surveyId")
    List<SurveyResponseWithUser> getResponsesForSurvey(int surveyId);
}
