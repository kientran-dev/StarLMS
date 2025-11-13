package com.starlms.starlms.model;

import com.starlms.starlms.entity.Grade;
import com.starlms.starlms.entity.Test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    private Test test;
    private Grade grade;
}
