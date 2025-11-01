package com.banew.cw2025_client.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class NetworkMonitor extends LiveData<Boolean> {

    private static NetworkMonitor INSTANCE;

    public static NetworkMonitor getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NetworkMonitor(context);
        }
        return INSTANCE;
    }

    private final ConnectivityManager cm;
    private final ConnectivityManager.NetworkCallback callback =
            new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    postValue(true);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    postValue(false);
                }
            };

    private NetworkMonitor(Context context) {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onActive() {
        cm.registerDefaultNetworkCallback(callback);
    }

    @Override
    protected void onInactive() {
        cm.unregisterNetworkCallback(callback);
    }
}

