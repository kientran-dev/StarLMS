package com.starlms.starlms.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.starlms.starlms.entity.SurveyResponse;
import com.starlms.starlms.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyResponseWithUser {
    @Embedded
    public SurveyResponse surveyResponse;

    @Relation(
            parentColumn = "user_id",
            entityColumn = "id"
    )
    public User user;
}
