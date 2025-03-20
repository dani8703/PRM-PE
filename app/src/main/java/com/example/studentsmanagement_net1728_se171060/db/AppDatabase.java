package com.example.studentsmanagement_net1728_se171060.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.studentsmanagement_net1728_se171060.dao.MajorDAO;
import com.example.studentsmanagement_net1728_se171060.dao.StudentDAO;
import com.example.studentsmanagement_net1728_se171060.model.Student;
import com.example.studentsmanagement_net1728_se171060.model.Major;


@Database(entities = {Major.class, Student.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MajorDAO majorDAO();
    public abstract StudentDAO studentDAO();
}
