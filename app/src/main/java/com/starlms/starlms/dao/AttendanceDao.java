package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.starlms.starlms.entity.Attendance;

@Dao
public interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Attendance attendance);

    @Query("SELECT * FROM attendance WHERE userId = :userId AND sessionId = :sessionId LIMIT 1")
    Attendance getAttendanceForUserAndSession(int userId, int sessionId);
}
