package com.example.sp25_trandangquocdat_njs1706.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.sp25_trandangquocdat_njs1706.model.Book;

import java.util.List;

@Dao
public interface BookDAO {
    @Query("SELECT * FROM book")
    List<Book> getAll();

    @Query("SELECT * FROM book WHERE id IN (:id)")
    Book getById(int id);

    @Query("SELECT * FROM book WHERE book_title LIKE :text")
    List<Book> findByTitle(String text);

    @Insert
    void insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("DELETE FROM book")
    void deleteAll();
}
