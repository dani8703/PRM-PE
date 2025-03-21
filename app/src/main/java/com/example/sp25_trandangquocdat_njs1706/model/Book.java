package com.example.sp25_trandangquocdat_njs1706.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "book", foreignKeys = @ForeignKey(entity = Author.class, parentColumns = "id", childColumns = "author_id", onDelete = ForeignKey.CASCADE))
public class Book implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "book_title")
    private String bookTitle;

    @ColumnInfo(name = "publication_date")
    private String publicationDate;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "author_id")
    private int authorId;

    public Book() {
    }

    public Book(String bookTitle, String publicationDate, String type, int authorId) {
        this.bookTitle = bookTitle;
        this.publicationDate = publicationDate;
        this.type = type;
        this.authorId = authorId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return bookTitle;
    }
}
