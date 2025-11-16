package com.starlms.starlms.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.starlms.starlms.entity.Course;
import com.starlms.starlms.entity.Teacher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseWithTeacher {
    @Embedded
    public Course course;

    @Relation(
            parentColumn = "teacher_id",
            entityColumn = "teacherId"
    )
    public Teacher teacher;
}
