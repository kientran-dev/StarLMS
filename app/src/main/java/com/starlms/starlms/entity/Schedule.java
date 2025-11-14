package com.starlms.starlms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Schedule {
    private final String datetime;
    private final String subject;
    private final String teacher;
    private final String classroom;
}
