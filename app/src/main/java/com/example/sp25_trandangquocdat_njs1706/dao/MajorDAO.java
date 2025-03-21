package com.example.sp25_trandangquocdat_njs1706.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.sp25_trandangquocdat_njs1706.model.Major;

import java.util.List;

@Dao
public interface MajorDAO {
    @Query("SELECT * FROM major")
    List<Major> getAll();

    @Query("SELECT * FROM major WHERE idMajor IN (:idMajor)")
    Major getById(int idMajor);

    @Query("SELECT * FROM major WHERE nameMajor LIKE :text")
    List<Major> findByName(String text);

    @Insert
    void insert(Major major);

    @Update
    void update(Major major);

    @Delete
    void delete(Major major);

    @Query("DELETE FROM major")
    void deleteAll();
}

