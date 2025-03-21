package com.example.sp25_trandangquocdat_njs1706.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.sp25_trandangquocdat_njs1706.dao.BookDAO;
import com.example.sp25_trandangquocdat_njs1706.dao.AuthorDAO;
import com.example.sp25_trandangquocdat_njs1706.model.Author;
import com.example.sp25_trandangquocdat_njs1706.model.Book;

@Database(entities = { Author.class, Book.class }, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDAO bookDAO();

    public abstract AuthorDAO authorDAO();
}
