package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.starlms.starlms.entity.Session;
import com.starlms.starlms.entity.SessionDetails;

import java.util.List;

@Dao
public interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Session session);

    @Update
    void update(Session session);

    @Delete
    void delete(Session session);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Session... sessions);

    @Query("SELECT * FROM sessions WHERE courseOwnerId = :courseId ORDER BY sessionDate ASC")
    List<Session> getSessionsForCourse(int courseId);

    @Transaction
    @Query("SELECT * FROM sessions WHERE courseOwnerId = :courseId")
    List<SessionDetails> getSessionDetailsForCourse(int courseId);
}
