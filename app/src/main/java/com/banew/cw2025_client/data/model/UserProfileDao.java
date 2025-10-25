package com.banew.cw2025_client.data.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserProfileDao {
    @Insert
    void insert(UserProfile user);

    @Query("SELECT * FROM UserProfile")
    List<UserProfile> getAllUsers();

    @Query("DELETE FROM UserProfile WHERE id = :id")
    void deleteById(int id);
}
