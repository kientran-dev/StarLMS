package com.starlms.starlms.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "notifications")
public class Notification {
    @PrimaryKey(autoGenerate = true)
    private int notificationId;
    private String message;

    // No-arg constructor for Room
    public Notification() {}

    public Notification(String message) {
        this.message = message;
    }
}
