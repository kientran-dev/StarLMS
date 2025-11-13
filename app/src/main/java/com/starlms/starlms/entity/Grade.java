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
@Entity(tableName = "grades",
        foreignKeys = {
            @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Test.class,
                        parentColumns = "testId",
                        childColumns = "testId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("userId"), @Index("testId")})
public class Grade {
    @PrimaryKey(autoGenerate = true)
    int gradeId;
    double score;
    int userId;
    int testId;
}
