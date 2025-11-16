package com.starlms.starlms.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(tableName = "leave_requests",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = CASCADE),
                @ForeignKey(entity = Course.class,
                        parentColumns = "courseId",
                        childColumns = "course_id",
                        onDelete = CASCADE)
        })
public class LeaveRequest {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "course_id", index = true)
    private long courseId;

    @ColumnInfo(name = "user_id", index = true)
    private long userId;

    private String reason;

    private long requestDate;

    private String status; // e.g., "Pending", "Approved", "Rejected"
}
