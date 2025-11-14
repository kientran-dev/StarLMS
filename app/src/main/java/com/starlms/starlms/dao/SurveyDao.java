package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.starlms.starlms.entity.Survey;

import java.util.List;

@Dao
public interface SurveyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Survey... surveys);

    /**
     * Gets all surveys, ordering them so that incomplete surveys appear first.
     */
    @Query("SELECT * FROM surveys ORDER BY isCompleted ASC, surveyId DESC")
    List<Survey> getAllSurveys();

    @Update
    void updateSurvey(Survey survey);

    @Query("SELECT * FROM surveys WHERE surveyId = :id")
    Survey findById(int id);
}
