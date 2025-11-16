package com.starlms.starlms.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.starlms.starlms.entity.UserCourseCrossRef;

@Dao
public interface UserCourseCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserCourseCrossRef crossRef);

}
