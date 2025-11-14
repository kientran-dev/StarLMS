package com.starlms.starlms.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "sessions",
        foreignKeys = {
                @ForeignKey(entity = Course.class,
                        parentColumns = "courseId",
                        childColumns = "courseOwnerId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Teacher.class,
                        parentColumns = "teacherId",
                        childColumns = "teacherId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index("courseOwnerId"), @Index("teacherId")})
public class Session {
    @PrimaryKey(autoGenerate = true)
    private int sessionId;
    private long sessionDate;
    private String title;
    private int courseOwnerId;

    private Integer teacherId; // Can be null if the teacher is deleted

    private String classroom;

    // No-arg constructor for Room
    public Session() {}

    // Constructor for creating a new session
    public Session(long sessionDate, String title, int courseOwnerId, Integer teacherId, String classroom) {
        this.sessionDate = sessionDate;
        this.title = title;
        this.courseOwnerId = courseOwnerId;
        this.teacherId = teacherId;
        this.classroom = classroom;
    }
}
