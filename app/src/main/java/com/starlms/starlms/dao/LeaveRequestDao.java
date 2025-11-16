package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.starlms.starlms.entity.LeaveRequest;
import com.starlms.starlms.model.LeaveRequestWithUser;

import java.util.List;

@Dao
public interface LeaveRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LeaveRequest leaveRequest);

    @Update
    void update(LeaveRequest leaveRequest);

    @Transaction
    @Query("SELECT * FROM leave_requests WHERE course_id = :courseId ORDER BY requestDate DESC")
    List<LeaveRequestWithUser> getLeaveRequestsForCourse(long courseId);
}
