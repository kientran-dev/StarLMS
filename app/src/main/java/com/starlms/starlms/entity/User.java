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
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    int id;
    String username;
    String password;
    String studentId;
    String fullName;
    String dateOfBirth;
    String gender;
    String phone;
    String address;
    String contactName;
    String contactPhone;
}
