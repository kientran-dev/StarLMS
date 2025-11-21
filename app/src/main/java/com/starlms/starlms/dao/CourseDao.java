package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.starlms.starlms.entity.Course;
import com.starlms.starlms.entity.User;
import com.starlms.starlms.model.CourseWithTeacher;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAndGetId(Course course);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Transaction
    @Query("SELECT * FROM courses")
    List<CourseWithTeacher> getCoursesWithTeachers();

    @Transaction
    @Query("SELECT T1.* FROM users AS T1 INNER JOIN user_course_cross_ref AS T2 ON T1.id = T2.user_id WHERE T2.courseId = :courseId")
    List<User> getUsersByCourse(long courseId);

    @Transaction
    @Query("SELECT * FROM courses WHERE courseId NOT IN (SELECT courseId FROM user_course_cross_ref WHERE user_id = :userId)")
    List<CourseWithTeacher> getUnregisteredCoursesForUser(long userId);
}
