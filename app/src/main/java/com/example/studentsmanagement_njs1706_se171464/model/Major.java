package com.example.studentsmanagement_njs1706_se171464.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "major")
public class Major implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idMajor")
    private int idMajor;

    @ColumnInfo(name = "nameMajor")
    private String nameMajor;

    public Major() {
    }

    public Major(String nameMajor) {
        this.nameMajor = nameMajor;
    }

    // Getters and setters
    public int getIdMajor() {
        return idMajor;
    }

    public void setIdMajor(int idMajor) {
        this.idMajor = idMajor;
    }

    public String getNameMajor() {
        return nameMajor;
    }

    public void setNameMajor(String nameMajor) {
        this.nameMajor = nameMajor;
    }

    @Override
    public String toString() {
        return nameMajor;
    }
}
