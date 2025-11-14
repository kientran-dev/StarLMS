package com.starlms.starlms.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "messages")
public class Message {
    @PrimaryKey(autoGenerate = true)
    private long messageId;
    private String text;
    private long timestamp;
    private long senderId; // Can be user or teacher ID
    private long receiverId; // Can be user or teacher ID
    private boolean isFromStudent; // True if sender is the student

    public Message(String text, long timestamp, long senderId, long receiverId, boolean isFromStudent) {
        this.text = text;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.isFromStudent = isFromStudent;
    }
}
