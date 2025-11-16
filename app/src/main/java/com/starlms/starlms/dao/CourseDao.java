package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.starlms.starlms.entity.Course;
import com.starlms.starlms.model.CourseWithTeacher;

import java.util.List;

@Dao
public interface CourseDao {

    @Insert
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Insert
    long insertAndGetId(Course course);

    @Query("SELECT * FROM courses")
    List<Course> getAllCourses();

    @Transaction
    @Query("SELECT * FROM courses")
    List<CourseWithTeacher> getCoursesWithTeachers();

    @Query("DELETE FROM courses")
    void clear();
}
