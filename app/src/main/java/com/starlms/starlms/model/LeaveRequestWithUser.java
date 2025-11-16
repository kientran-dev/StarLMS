package com.starlms.starlms.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.starlms.starlms.entity.LeaveRequest;
import com.starlms.starlms.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveRequestWithUser {
    @Embedded
    public LeaveRequest leaveRequest;

    @Relation(
            parentColumn = "user_id",
            entityColumn = "id"
    )
    public User user;
}
