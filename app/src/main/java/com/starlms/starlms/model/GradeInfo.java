package com.starlms.starlms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GradeInfo {
    private String testName;
    private double score;
    private int maxScore;
}
