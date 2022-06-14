package com.example.googlesearchschool2.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "schoolName")
    public String schoolName;

    @ColumnInfo(name = "schoolInfo")
    public String schoolInfo;

    @ColumnInfo(name = "schoolLatitude")
    public Double schoolLatitude;

    @ColumnInfo(name = "schoolLongitude")
    public Double schoolLongitude;

    @ColumnInfo(name = "ColorImage")
    public int ColorImage;
}