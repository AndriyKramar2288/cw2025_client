package com.banew.cw2025_client.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.banew.cw2025_client.data.model.AppDatabase;
import com.banew.cw2025_client.data.model.LoggedInUser;

import java.io.IOException;

public class DataSource {
    private final SharedPreferences prefs;
    private final AppDatabase db;

    public DataSource(Context applicationContext) {
        db = Room.databaseBuilder(
                applicationContext,
                AppDatabase.class,
                "my-database"
        ).build();

        prefs = applicationContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
    }

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}