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
@Entity(tableName = "tests",
        foreignKeys = @ForeignKey(entity = Course.class,
                                  parentColumns = "courseId",
                                  childColumns = "courseOwnerId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("courseOwnerId")})
public class Test {
    @PrimaryKey(autoGenerate = true)
    int testId;
    String testName; // e.g., "Test 1", "Mid-term Exam"
    int maxScore;
    int courseOwnerId;
}
