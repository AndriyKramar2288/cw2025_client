package com.banew.cw2025_client.ui.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.courses.CourseBasicDto
import com.banew.cw2025_backend_common.dto.courses.CoursePlanCourseDto
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.DataSource.TopicForm
import com.banew.cw2025_client.data.NetworkMonitor
import com.banew.cw2025_client.data.Result
import kotlinx.coroutines.launch
import java.time.Instant

class MainPageModel(val mock : Boolean = false) : ViewModel() {
    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource

    var currentUser = mutableStateOf<UserProfileBasicDto?>(null)
        private set
    var currentCoursePlans =
        mutableStateOf<List<CoursePlanBasicDto>>(ArrayList())
        private set

    var currentCourses =
        mutableStateOf<List<CourseBasicDto>>(ArrayList())
        private set

    var lastException = mutableStateOf<Exception?>(null)
        private set

    var preferredRoute = mutableStateOf("home")
        private set

    private val networkMonitor: NetworkMonitor? = if (!mock) NetworkMonitor.getInstance(
        GlobalApplication.getInstance().applicationContext
    ) else null

    private val networkObserver = Observer { connected: Boolean ->
        if (connected) refresh()
    }

    init {
        refresh()
        networkMonitor?.observeForever(networkObserver)
    }

    override fun onCleared() {
        super.onCleared()

        networkMonitor?.removeObserver(networkObserver)
    }

    fun beginCourse(coursePLanId: Long) {
        if(!mock && dataSource != null) viewModelScope.launch {
            val result = dataSource.beginCourse(coursePLanId)

            if (result.isSuccess) {
                currentCourses.value += listOf(result.asSuccess().data)
                preferredRoute.value = "courses"
            }
            else {
                lastException.value = result.asError().error
            }
        }
    }

    fun createCoursePlan(
        name: String,
        desc: String,
        topics: List<TopicForm>
    ) {
        if(!mock && dataSource != null) viewModelScope.launch {
            val planRes = dataSource.createCoursePlan(name, desc, topics)

            when (planRes) {
                is Result.Success -> {
                    refresh()
                    preferredRoute.value = "home"
                }
                is Result.Error -> {
                    lastException.value = planRes.asError().error
                }
            }
        }
    }

    fun refresh(callback: () -> Unit = {}) {

        if (mock) {
            currentCoursePlans.value = listOf(
                CoursePlanBasicDto(
                    3, "Курс", UserProfileBasicDto(
                        "Користувач", "aboba@gmail.com", "qwewqweq"
                    ), "wqeqeqwwq", listOf(
                        CoursePlanBasicDto.TopicBasicDto(
                            null, "тема 1", "опис"
                        )
                    )
                )
            ).flatMap { listOf(it, it, it, it, it) }.flatMap { listOf(it, it, it) }

            currentCourses.value = listOf(
                CourseBasicDto(
                    Instant.now(), CoursePlanCourseDto(
                        null, "Курс", UserProfileBasicDto(
                            "Користувач", "aboba@gmail.com", "qwewqweq"
                        ), "wqeqeqwwq"
                    ),
                    listOf()
                )
            )

            return
        }

        viewModelScope.launch {
            val userRes = dataSource!!.currentUserProfile()
            val plansRes = dataSource.currentCoursePlanList()
            val coursesRes = dataSource.currentCourseList()

            when (userRes) {
                is Result.Success -> currentUser.value = userRes.data
                is Result.Error -> lastException.value = userRes.error
            }

            when (plansRes) {
                is Result.Success -> currentCoursePlans.value = plansRes.data
                is Result.Error -> lastException.value = plansRes.error
            }

            when (coursesRes) {
                is Result.Success -> currentCourses.value = coursesRes.data
                is Result.Error -> lastException.value = coursesRes.error
            }

            callback()
        }
    }

    val isShouldToSwitchToLogin: Boolean
        get() = dataSource?.token == null
}
