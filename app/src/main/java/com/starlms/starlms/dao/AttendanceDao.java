package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.starlms.starlms.entity.Attendance;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Attendance attendance);

    @Query("SELECT * FROM attendance WHERE userId = :userId AND sessionId = :sessionId LIMIT 1")
    Attendance getAttendanceForUserAndSession(int userId, int sessionId);

    @Query("SELECT T1.* FROM attendance AS T1 INNER JOIN sessions AS T2 ON T1.sessionId = T2.sessionId WHERE T1.userId = :userId AND T2.courseOwnerId = :courseId")
    List<Attendance> getAttendanceForUserInCourse(int userId, int courseId);
}
