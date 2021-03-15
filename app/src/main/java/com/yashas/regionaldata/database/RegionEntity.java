package com.yashas.regionaldata.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "regionData")
public class RegionEntity {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "capital")
    public String capital;

    @ColumnInfo(name = "flag")
    public String flag;

    @ColumnInfo(name = "region")
    public String region;

    @ColumnInfo(name = "subRegion")
    public String subRegion;

    @ColumnInfo(name = "population")
    public Long population;

    @ColumnInfo(name = "borders")
    public String borders;

    @ColumnInfo(name = "languages")
    public String languages;

    public RegionEntity(String name, String capital, String flag, String region, String subRegion, Long population, String borders, String languages){
        this.id = null;
        this.name = name;
        this.capital = capital;
        this.flag = flag;
        this.region = region;
        this.subRegion = subRegion;
        this.population = population;
        this.borders = borders;
        this.languages = languages;
    }
}