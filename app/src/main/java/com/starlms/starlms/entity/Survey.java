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
    private boolean isCompleted;

    // Room uses this constructor
    public Survey() {}

    // This constructor is ignored by Room and used for creating new instances
    @Ignore
    public Survey(String title, String description) {
        this.title = title;
        this.description = description;
        this.isCompleted = false; // Default status
    }
}
