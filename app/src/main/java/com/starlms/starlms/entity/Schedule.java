package com.starlms.starlms.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Schedule {
    private final long sessionId;
    private final String subject;
    private final Date dateTime;
    private final String teacherInfo;
    private final String classroom;
}
