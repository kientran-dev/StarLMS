package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.starlms.starlms.entity.Grade;

@Dao
public interface GradeDao {
    @Insert
    void insertAll(Grade... grades);

    @Query("SELECT * FROM grades WHERE userId = :userId AND testId = :testId LIMIT 1")
    Grade getGradeForUserAndTest(int userId, int testId);
}
