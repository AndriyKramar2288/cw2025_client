package com.banew.cw2025_client.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto;
import com.banew.cw2025_client.GlobalApplication;
import com.banew.cw2025_client.data.DataSource;
import com.banew.cw2025_client.data.NetworkMonitor;
import com.banew.cw2025_client.data.Result;

public class MainPageModel extends ViewModel {
    private final DataSource dataSource;

    private final MutableLiveData<UserProfileBasicDto> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Result<?>> lastResult = new MutableLiveData<>();
    private final NetworkMonitor networkMonitor;
    private final Observer<Boolean> networkObserver = connected -> {
        if (connected) refreshProfile();
    };

    public MainPageModel() {
        dataSource = GlobalApplication.getInstance().getDataSource();
        networkMonitor = NetworkMonitor.getInstance(
                GlobalApplication.getInstance().getApplicationContext()
        );

        refreshProfile();

        networkMonitor.observeForever(networkObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        networkMonitor.removeObserver(networkObserver);
    }

    public void refreshProfile() {
        observeOnce(dataSource.getCurrentUserProfile(), r -> {
            lastResult.postValue(r);
            if (r.isSuccess()) currentUser.postValue(r.asSuccess().getData());
        });
    }

    public static <T> void observeOnce(LiveData<T> liveData, Observer<T> observer) {
        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                liveData.removeObserver(this);
                observer.onChanged(t);
            }
        });
    }

    public LiveData<UserProfileBasicDto> getCurrentUser() {
        return currentUser;
    }

    public boolean isShouldToSwitchToLogin() {
        return dataSource.getToken() == null;
    }

    public LiveData<Result<?>> getLastResult() {
        return lastResult;
    }
}
