package com.yashas.regionaldata.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RegionDao {
    @Insert
    void insertIntoDb(RegionEntity regionEntity);

    @Query("SELECT * FROM regionData")
    List<RegionEntity> getAll();

    @Query("DELETE FROM regionData")
    void deleteAll();
}
