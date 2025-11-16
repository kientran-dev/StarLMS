package com.starlms.starlms.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
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
@Entity(tableName = "courses",
        foreignKeys = @ForeignKey(entity = Teacher.class,
                                  parentColumns = "teacherId",
                                  childColumns = "teacher_id",
                                  onDelete = CASCADE))
public class Course {
    @PrimaryKey(autoGenerate = true)
    int courseId;
    String name;
    String type; // "online" or "offline"

    @ColumnInfo(name = "teacher_id", index = true)
    int teacherId;
}
