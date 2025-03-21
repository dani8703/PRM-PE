package com.example.sp25_trandangquocdat_njs1706.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.sp25_trandangquocdat_njs1706.dao.MajorDAO;
import com.example.sp25_trandangquocdat_njs1706.dao.StudentDAO;
import com.example.sp25_trandangquocdat_njs1706.model.Student;
import com.example.sp25_trandangquocdat_njs1706.model.Major;


@Database(entities = {Major.class, Student.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MajorDAO majorDAO();
    public abstract StudentDAO studentDAO();
}
