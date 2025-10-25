package com.banew.cw2025_client.data.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserProfile.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserProfileDao userDao();
}
