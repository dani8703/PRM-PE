package com.example.sp25_trandangquocdat_njs1706.data;


import com.example.sp25_trandangquocdat_njs1706.db.AppDatabase;
import com.example.sp25_trandangquocdat_njs1706.excutors.AppExecutors;
import com.example.sp25_trandangquocdat_njs1706.model.Author;
import com.example.sp25_trandangquocdat_njs1706.model.Book;

public class GlobalData {
    private static GlobalData instance;

    private Author author;
    private AppDatabase appDatabase;
    private GlobalData() {}
    private GlobalData(Author student) {
        this.author = student;
    }

    public static synchronized GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }


    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    public void setAppDatabase(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public void save(Author author) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.authorDAO().insert(author);
        });
    }

    public void update(Author author) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.authorDAO().update(author);
        });
    }

    public void save(Book childModel) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.bookDAO().insert(childModel);
        });
    }

    public void update(Book childModel) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.bookDAO().update(childModel);
        });
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}