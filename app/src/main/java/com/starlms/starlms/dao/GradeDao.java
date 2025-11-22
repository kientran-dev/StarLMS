package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.starlms.starlms.entity.Grade;

@Dao
public interface GradeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Grade grade);

    @Insert
    void insertAll(Grade... grades);

    @Query("SELECT * FROM grades WHERE userId = :userId AND testId = :testId LIMIT 1")
    Grade getGradeForTest(int userId, int testId);
}
