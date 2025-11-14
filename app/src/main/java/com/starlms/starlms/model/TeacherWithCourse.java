package com.starlms.starlms.model;

import androidx.room.Embedded;
import com.starlms.starlms.entity.Course;
import com.starlms.starlms.entity.Teacher;

import lombok.Data;

@Data
public class TeacherWithCourse {
    @Embedded
    private Teacher teacher;

    @Embedded(prefix = "course_")
    private Course course;
}
