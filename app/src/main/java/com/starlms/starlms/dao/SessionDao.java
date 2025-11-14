package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.starlms.starlms.entity.Session;
import com.starlms.starlms.entity.SessionDetails;

import java.util.List;

@Dao
public interface SessionDao {
    @Insert
    void insertAll(Session... sessions);

    @Query("SELECT * FROM sessions WHERE courseOwnerId = :courseId")
    List<Session> getSessionsForCourse(int courseId);

    @Transaction
    @Query("SELECT * FROM sessions WHERE courseOwnerId = :courseId")
    List<SessionDetails> getSessionDetailsForCourse(int courseId);
}
