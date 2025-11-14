package com.starlms.starlms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GradeInfo {
    private String testName;
    private double score;
    private int maxScore;
}
