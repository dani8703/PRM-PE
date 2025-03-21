package com.example.sp25_trandangquocdat_njs1706.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.sp25_trandangquocdat_njs1706.model.Author;

import java.util.List;

@Dao
public interface AuthorDAO {
    @Query("SELECT * FROM author")
    List<Author> getAll();

    @Query("SELECT * FROM author WHERE id IN (:id)")
    Author getById(int id);

    @Query("SELECT * FROM author WHERE name LIKE :text")
    List<Author> findByName(String text);

    @Insert
    void insert(Author author);

    @Update
    void update(Author author);

    @Delete
    void delete(Author author);

    @Query("DELETE FROM author")
    void deleteAll();
}
