package com.banew.cw2025_client.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.NetworkMonitor
import com.banew.cw2025_client.data.Result
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class MainPageModel : ViewModel() {
    private val dataSource: DataSource = GlobalApplication.getInstance().dataSource

    var currentUser = mutableStateOf<UserProfileBasicDto?>(null)
        private set
    var currentCoursePlans =
        mutableStateOf<MutableList<CoursePlanBasicDto?>?>(ArrayList<CoursePlanBasicDto?>())
        private set
    var lastException = mutableStateOf<Exception?>(null)
        private set

    private val networkMonitor: NetworkMonitor = NetworkMonitor.getInstance(
        GlobalApplication.getInstance().applicationContext
    )

    private val networkObserver = Observer { connected: Boolean ->
        if (connected) refresh()
    }

    init {
        refresh()
        networkMonitor.observeForever(networkObserver)
    }

    override fun onCleared() {
        super.onCleared()

        networkMonitor.removeObserver(networkObserver)
    }

    fun refresh(callback: () -> Unit = {}) {
        val f1 = dataSource.getCurrentUserProfile()
            .thenAccept { r ->
                when {
                    r.isSuccess -> currentUser.value = r.asSuccess().data
                    else -> lastException.value = r.asError().error
                }
            }

        val f2 = dataSource.getCurrentCoursePlanList()
            .thenAccept { r ->
                when {
                    r.isSuccess ->
                        currentCoursePlans.value = r.asSuccess().data.toMutableList()
                    else -> lastException.value = r.asError().error
                }
            }

        CompletableFuture.allOf(f1, f2).thenAccept { callback() }
    }

    val isShouldToSwitchToLogin: Boolean
        get() = dataSource.token == null
}
