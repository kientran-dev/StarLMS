package com.starlms.starlms.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "teachers")
public class Teacher {
    @PrimaryKey(autoGenerate = true)
    private int teacherId;
    private String name;
    private String email;

    public Teacher(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
