package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.starlms.starlms.entity.Notification;

import java.util.List;

@Dao
public interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Notification... notifications);

    @Query("SELECT * FROM notifications ORDER BY notificationId DESC")
    List<Notification> getAllNotifications();
}
