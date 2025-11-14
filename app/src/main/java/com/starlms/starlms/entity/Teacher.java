package com.starlms.starlms.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "teachers",
        indices = {@Index(value = {"email"}, unique = true), @Index(value = {"phone_number"}, unique = true)})
public class Teacher {
    @PrimaryKey(autoGenerate = true)
    private int teacherId;
    private String name;
    private String email;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    public Teacher() {}

    public Teacher(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
