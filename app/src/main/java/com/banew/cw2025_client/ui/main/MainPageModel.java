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

    private final MutableLiveData<Result<?>> lastResult = new MutableLiveData<>();
    public LiveData<Result<?>> getLastResult() {
        return lastResult;
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
        observeOnce(dataSource.getCurrentUserProfile(), r -> {
            if (r.isSuccess()) currentUser.postValue(r.asSuccess().getData());
        });
        observeOnce(dataSource.getCurrentCoursePlanList(), r -> {
            if (r.isSuccess()) currentCoursePlans.postValue(r.asSuccess().getData());
        });
    }

    public <T> void observeOnce(LiveData<T> liveData, Observer<T> observer) {
        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                liveData.removeObserver(this);
                lastResult.postValue((Result<?>) t);
                observer.onChanged(t);
            }
        });
    }

    public boolean isShouldToSwitchToLogin() {
        return dataSource.getToken() == null;
    }
}
