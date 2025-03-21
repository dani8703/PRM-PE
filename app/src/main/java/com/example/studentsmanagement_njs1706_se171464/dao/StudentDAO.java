package com.example.studentsmanagement_njs1706_se171464.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentsmanagement_njs1706_se171464.model.Student;

import java.util.List;

@Dao
public interface StudentDAO {
    @Query("SELECT * FROM student")
    List<Student> getAll();

    @Query("SELECT * FROM student WHERE id IN (:id)")
    Student getById(int id);

    @Query("SELECT * FROM student WHERE name LIKE :text")
    List<Student> findByName(String text);

    @Insert
    void insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);

    @Query("DELETE FROM student")
    void deleteAll();
}

