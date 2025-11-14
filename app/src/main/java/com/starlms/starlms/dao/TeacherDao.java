package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.starlms.starlms.entity.Teacher;
import com.starlms.starlms.model.TeacherWithCourse;

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

    @Query("SELECT DISTINCT T.*, C.courseId as course_courseId, C.name as course_name, C.type as course_type " +
           "FROM teachers T " +
           "INNER JOIN sessions S ON T.teacherId = S.teacherId " +
           "INNER JOIN courses C ON S.courseOwnerId = C.courseId")
    List<TeacherWithCourse> getTeachersWithCourses();

}
