package com.starlms.starlms.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.starlms.starlms.entity.Course;
import com.starlms.starlms.entity.User;
import com.starlms.starlms.entity.UserCourseCrossRef;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithCourses {
    @Embedded
    public User user;

    @Relation(
            parentColumn = "id", // Cột trong bảng User
            entity = Course.class,
            entityColumn = "courseId", // Cột trong bảng Course
            associateBy = @Junction(
                    value = UserCourseCrossRef.class,
                    parentColumn = "user_id", // Cột trong bảng nối trỏ đến User
                    entityColumn = "courseId"  // Cột trong bảng nối trỏ đến Course
            )
    )
    public List<Course> courses;
}
