package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.starlms.starlms.entity.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Query("SELECT * FROM messages WHERE (senderId = :userId AND receiverId = :teacherId) OR (senderId = :teacherId AND receiverId = :userId) ORDER BY timestamp ASC")
    List<Message> getMessagesBetween(long userId, long teacherId);
}
