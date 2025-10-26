package com.banew.cw2025_client.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.banew.cw2025_backend_common.dto.UserProfileBasicDto;
import com.banew.cw2025_client.GlobalApplication;
import com.banew.cw2025_client.data.DataSource;

public class MainPageModel extends ViewModel {
    private final DataSource dataSource;

    private final MutableLiveData<UserProfileBasicDto> currentUser = new MutableLiveData<>();

    public MainPageModel() {
        dataSource = GlobalApplication.getInstance().getDataSource();
        dataSource.getCurrentUserProfile().observeForever(r -> {
            if (r.isSuccess()) currentUser.postValue(r.asSuccess().getData());
        });
    }

    public LiveData<UserProfileBasicDto> getCurrentUser() {
        return currentUser;
    }

    public boolean isShouldToSwitchToLogin() {
        return dataSource.getToken() == null;
    }
}
