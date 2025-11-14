package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.starlms.starlms.entity.Teacher;

import java.util.List;

@Dao
public interface TeacherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Teacher... teachers);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAndGetId(Teacher teacher);

    @Query("SELECT * FROM teachers WHERE teacherId = :id")
    Teacher findById(int id);

    @Query("SELECT * FROM teachers")
    List<Teacher> getAll();
}
