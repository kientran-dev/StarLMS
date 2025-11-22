package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.starlms.starlms.entity.Test;

import java.util.List;

@Dao
public interface TestDao {
    @Insert
    long insert(Test test);

    @Insert
    List<Long> insertAll(Test... tests);

    @Insert
    long insertAndGetId(Test test);

    @Query("SELECT * FROM tests WHERE courseOwnerId = :courseId")
    List<Test> getTestsForCourse(int courseId);
}
