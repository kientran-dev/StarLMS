package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.starlms.starlms.entity.Survey;

import java.util.List;

@Dao
public interface SurveyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Survey survey);

    @Update
    void update(Survey survey);

    @Delete
    void delete(Survey survey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Survey... surveys); // THÊM LẠI PHƯƠNG THỨC NÀY

    @Query("SELECT * FROM surveys WHERE status = :status ORDER BY surveyId DESC")
    List<Survey> getSurveysByStatus(String status);

    @Query("SELECT * FROM surveys WHERE surveyId = :id")
    Survey findById(int id);
}
