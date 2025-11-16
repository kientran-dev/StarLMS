package com.starlms.starlms.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "user_course_cross_ref",
        primaryKeys = {"user_id", "courseId"},
        foreignKeys = {
            @ForeignKey(entity = User.class,
                    parentColumns = "id",
                    childColumns = "user_id",
                    onDelete = CASCADE),
            @ForeignKey(entity = Course.class,
                    parentColumns = "courseId",
                    childColumns = "courseId",
                    onDelete = CASCADE)
        })
public class UserCourseCrossRef {

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(index = true)
    public int courseId;

    public UserCourseCrossRef(long userId, int courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }
}
