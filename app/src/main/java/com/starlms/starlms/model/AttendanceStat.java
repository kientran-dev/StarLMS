package com.starlms.starlms.model;

import com.starlms.starlms.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceStat {
    private User user;
    private int totalSessions;
    private int presentCount;
    private int leaveCount;

    public AttendanceStat(User user) {
        this.user = user;
    }

    public int getAbsentCount() {
        return totalSessions - presentCount - leaveCount;
    }
}
