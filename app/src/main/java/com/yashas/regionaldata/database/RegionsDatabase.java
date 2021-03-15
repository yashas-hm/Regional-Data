package com.yashas.regionaldata.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RegionEntity.class}, version = 1)
public abstract class RegionsDatabase extends RoomDatabase {
    public abstract RegionDao regionDao();
}
