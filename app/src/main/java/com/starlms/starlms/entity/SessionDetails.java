package com.starlms.starlms.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionDetails {
    @Embedded
    public Session session;

    @Relation(
            parentColumn = "teacherId",
            entityColumn = "teacherId"
    )
    public Teacher teacher;
}
