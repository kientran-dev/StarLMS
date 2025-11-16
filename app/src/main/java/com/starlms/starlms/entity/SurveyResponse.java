package com.starlms.starlms.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(tableName = "survey_responses",
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
public class SurveyResponse {
    @PrimaryKey(autoGenerate = true)
    private int responseId;

    @ColumnInfo(name = "survey_id", index = true)
    private int surveyId;

    @ColumnInfo(name = "user_id", index = true)
    private long userId;

    private String responseText;

    private long submissionDate; // THÊM TRƯỜNG NÀY
}
