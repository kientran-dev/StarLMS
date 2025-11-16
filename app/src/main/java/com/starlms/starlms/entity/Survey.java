package com.starlms.starlms.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "surveys")
public class Survey {
    @PrimaryKey(autoGenerate = true)
    private int surveyId;
    private String title;
    private String description;
    private String status; // "Chưa đăng" hoặc "Đã đăng"

    // Room uses this constructor
    public Survey() {}

    @Ignore
    public Survey(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = "Chưa đăng"; // Default status
    }
}
