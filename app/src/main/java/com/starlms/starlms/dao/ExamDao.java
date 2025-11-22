package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.starlms.starlms.entity.Exam;

import java.util.List;

@Dao
public interface ExamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Exam exam);

    @Query("SELECT * FROM exams WHERE userId = :userId AND courseId = :courseId")
    List<Exam> getExamsForCourse(int userId, int courseId);
}
