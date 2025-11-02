package com.banew.cw2025_client.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto;
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto;
import com.banew.cw2025_client.GlobalApplication;
import com.banew.cw2025_client.data.DataSource;
import com.banew.cw2025_client.data.NetworkMonitor;
import com.banew.cw2025_client.data.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainPageModel extends ViewModel {
    private final DataSource dataSource;

    private final MutableLiveData<UserProfileBasicDto> currentUser = new MutableLiveData<>();
    public LiveData<UserProfileBasicDto> getCurrentUser() {
        return currentUser;
    }

    private final MutableLiveData<List<CoursePlanBasicDto>> currentCoursePlans = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<CoursePlanBasicDto>> getCurrentCoursePlans() {
        return currentCoursePlans;
    }

    private final MutableLiveData<Exception> lastException = new MutableLiveData<>();
    public LiveData<Exception> getLastException() {
        return lastException;
    }


    private final NetworkMonitor networkMonitor;
    private final Observer<Boolean> networkObserver = connected -> {
        if (connected) refresh();
    };

    public MainPageModel() {
        dataSource = GlobalApplication.getInstance().getDataSource();
        networkMonitor = NetworkMonitor.getInstance(
                GlobalApplication.getInstance().getApplicationContext()
        );

        refresh();

        networkMonitor.observeForever(networkObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        networkMonitor.removeObserver(networkObserver);
    }

    public void refresh() {
        refresh(() -> {});
    }

    public void refresh(Runnable callback) {
        var f1 = dataSource.getCurrentUserProfile().thenAccept(r -> {
            r.resolveData(currentUser, lastException);
        });

        var f2 = dataSource.getCurrentCoursePlanList().thenAccept(r -> {
            r.resolveData(currentCoursePlans, lastException);
        });

        CompletableFuture.allOf(f1, f2).thenAccept(r -> callback.run());
    }

    public boolean isShouldToSwitchToLogin() {
        return dataSource.getToken() == null;
    }
}
