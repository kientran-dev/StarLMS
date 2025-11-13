package com.starlms.starlms.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(tableName = "questions",
        foreignKeys = @ForeignKey(entity = Test.class,
                                  parentColumns = "testId",
                                  childColumns = "testOwnerId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("testOwnerId")})
public class Question {
    @PrimaryKey(autoGenerate = true)
    int questionId;
    String questionText;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    int correctOption; // 1 for A, 2 for B, etc.
    int testOwnerId;
}
