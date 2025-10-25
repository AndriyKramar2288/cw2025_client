package com.banew.cw2025_client;

import android.app.Application;

import com.banew.cw2025_client.data.DataSource;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;
    private DataSource dataSource;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        dataSource = new DataSource(getApplicationContext());
    }

    public static GlobalApplication getInstance() {
        return instance;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
