package com.starlms.starlms.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(tableName = "courses")
public class Course {
    @PrimaryKey(autoGenerate = true)
    int courseId;
    String name;
    String type; // "online" or "offline"
}
