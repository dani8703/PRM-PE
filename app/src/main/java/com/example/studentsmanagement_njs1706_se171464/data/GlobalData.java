package com.example.studentsmanagement_njs1706_se171464.data;


import com.example.studentsmanagement_njs1706_se171464.db.AppDatabase;
import com.example.studentsmanagement_njs1706_se171464.excutors.AppExecutors;
import com.example.studentsmanagement_njs1706_se171464.model.Student;
import com.example.studentsmanagement_njs1706_se171464.model.Major;

public class GlobalData {
    private static GlobalData instance;

    private Student student;
    private AppDatabase appDatabase;
    private GlobalData() {}
    private GlobalData(Student student) {
        this.student = student;
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

    public void save(Student student) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.studentDAO().insert(student);
        });
    }

    public void update(Student student) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.studentDAO().update(student);
        });
    }

    public void save(Major childModel) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.majorDAO().insert(childModel);
        });
    }

    public void update(Major childModel) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.majorDAO().update(childModel);
        });
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student Student) {
        this.student = Student;
    }
}