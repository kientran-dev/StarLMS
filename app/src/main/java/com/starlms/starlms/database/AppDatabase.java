package com.starlms.starlms.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.starlms.starlms.dao.AttendanceDao;
import com.starlms.starlms.dao.CourseDao;
import com.starlms.starlms.dao.GradeDao;
import com.starlms.starlms.dao.MessageDao;
import com.starlms.starlms.dao.NotificationDao;
import com.starlms.starlms.dao.QuestionDao;
import com.starlms.starlms.dao.SessionDao;
import com.starlms.starlms.dao.SurveyDao;
import com.starlms.starlms.dao.TeacherDao;
import com.starlms.starlms.dao.TestDao;
import com.starlms.starlms.dao.UserDao;
import com.starlms.starlms.entity.Attendance;
import com.starlms.starlms.entity.Course;
import com.starlms.starlms.entity.Grade;
import com.starlms.starlms.entity.Message;
import com.starlms.starlms.entity.Notification;
import com.starlms.starlms.entity.Question;
import com.starlms.starlms.entity.Session;
import com.starlms.starlms.entity.Survey;
import com.starlms.starlms.entity.Teacher;
import com.starlms.starlms.entity.Test;
import com.starlms.starlms.entity.User;

@Database(entities = {User.class, Course.class, Session.class, Attendance.class, Test.class, Grade.class, Question.class, Teacher.class, Survey.class, Notification.class, Message.class}, version = 12, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CourseDao courseDao();
    public abstract SessionDao sessionDao();
    public abstract AttendanceDao attendanceDao();
    public abstract TestDao testDao();
    public abstract GradeDao gradeDao();
    public abstract QuestionDao questionDao();
    public abstract TeacherDao teacherDao();
    public abstract SurveyDao surveyDao();
    public abstract NotificationDao notificationDao();
    public abstract MessageDao messageDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "starlms_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
