package com.banew.cw2025_client.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserProfile {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String email;
}
