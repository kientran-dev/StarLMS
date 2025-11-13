package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.starlms.starlms.entity.Course;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    void insertAll(Course... courses);

    @Insert
    long insertAndGetId(Course course);

    @Query("SELECT * FROM courses")
    List<Course> getAllCourses();

    @Query("DELETE FROM courses")
    void clear();
}
