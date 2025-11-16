package com.starlms.starlms.entity;

import static androidx.room.ForeignKey.CASCADE;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity(tableName = "user_survey_completions",
        primaryKeys = {"user_id", "survey_id"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = CASCADE),
                @ForeignKey(entity = Survey.class,
                        parentColumns = "surveyId",
                        childColumns = "survey_id",
                        onDelete = CASCADE)
        })
@AllArgsConstructor
@NoArgsConstructor
public class UserSurveyCompletion {

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "survey_id", index = true)
    public int surveyId;
}
