package com.banew.cw2025_client.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banew.cw2025_backend_common.dto.cards.FlashCardDayStats
import com.banew.cw2025_backend_common.dto.cards.FlashCardType
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.courses.CourseBasicDto
import com.banew.cw2025_backend_common.dto.courses.CoursePlanCourseDto
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.NetworkMonitor
import com.banew.cw2025_client.data.Result
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

class MainPageModel(isMock: Boolean = false) : ViewModel() {
    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource

    var searchQuery by mutableStateOf("")
    var isRefreshing by mutableStateOf(false)
    var isShouldToSwitchToLogin by mutableStateOf(false)
        private set
    var isConnectionError by mutableStateOf(false)
        private set
    var lastException by mutableStateOf<Exception?>(null)
    var preferredRouteCallback by mutableStateOf<(proceed: () -> Unit) -> Unit>(
        { proceed -> proceed() }
    )
    private val preferredRouteState = mutableStateOf("courses")
    var shouldRefreshCourses by mutableStateOf(true)
    var shouldRefreshCoursePlans by mutableStateOf(true)
    var currentCoursePlans by
        mutableStateOf<List<CoursePlanBasicDto>>(
            if (!isMock) emptyList() else listOf(
                CoursePlanBasicDto(
                    3, "Курс", UserProfileBasicDto(
                        44L,
                        "Користувач", "aboba@gmail.com", "qwewqweq"
                    ), "wqeqeqwwq", listOf(
                        CoursePlanBasicDto.TopicBasicDto(
                            null, "тема 1", "опис"
                        )
                    )
                )
            ).flatMap { listOf(it, it, it, it, it) }
        )
        private set
    var currentCourses by
        mutableStateOf<List<CourseBasicDto>>(if (!isMock) emptyList() else listOf(
            CourseBasicDto(
                148228L,
                Instant.parse("2025-11-07T22:28:26.935362Z"),
                CoursePlanCourseDto(
                    1603L,
                    "First normal Course",
                    UserProfileBasicDto(
                        2L,
                        "Banewko",
                        "andriykramar465@gmail.com",
                        "https://cdn.omlet.com/images/originals/breed_abyssinian_cat.jpg"
                    ),
                    "Desc for the first normal course"
                ),
                "qwewq",
                4233, 13
            )
        ).flatMap { listOf(it, it, it) })
        private set
    var flashCardDayStats by
        mutableStateOf(if (!isMock) null else FlashCardDayStats(
            mapOf(
                FlashCardType.NEW to 1,
                FlashCardType.REPEAT to 2,
                FlashCardType.STUDY to 3
            ),
            10,
            Duration.ofMinutes(5)
        ))
        private set


    var preferredRoute
        get() = preferredRouteState.value
        set(value) {
            preferredRouteCallback {
                viewModelScope.launch {
                    isRefreshing = true
                    when (value) {
                        "courses" -> refreshCoursePage()
                        "home" -> refreshCoursePlanPage()
                    }
                    preferredRouteState.value = value
                    isRefreshing = false
                }
            }
            preferredRouteCallback = { proceed -> proceed() }
        }

    private val networkMonitor: NetworkMonitor? = if (!isMock) NetworkMonitor.getInstance(
        GlobalApplication.getInstance().applicationContext
    ) else null

    private val networkObserver = Observer { connected: Boolean ->
        if (connected) {
            updateConnectionError(false)
            refresh()
        }
    }

    init {
        if (!isMock) {
            networkMonitor?.observeForever(networkObserver)
        }
    }

    override fun onCleared() {
        super.onCleared()

        networkMonitor?.removeObserver(networkObserver)
    }

    fun logout() {
        dataSource!!.logout()
        isShouldToSwitchToLogin = true
    }

    fun searchCoursePlans() {
        viewModelScope.launch {
            refreshCoursePlanPage(true)
        }
    }

    fun beginCourse(coursePLanId: Long) {
        viewModelScope.launch {
            dataSource!!.beginCourse(coursePLanId).asSuccess {
                currentCourses += listOf(it.data)
                shouldRefreshCourses = true
                preferredRoute = "courses"
            }.default(this@MainPageModel)
        }
    }

    fun confirmCoursePlanCreation(dto: CoursePlanBasicDto) {
        currentCoursePlans += listOf(dto)
        preferredRoute = "home"
        shouldRefreshCoursePlans = true
    }

    private suspend fun refreshCoursePlanPage(forced: Boolean = false) {
        if (!shouldRefreshCoursePlans && !forced) return

        shouldRefreshCoursePlans = false

        dataSource!!.currentCoursePlanList(searchQuery).asSuccess {
            currentCoursePlans = it.data
        }.asNetEx()
    }

    private suspend fun refreshCoursePage(forced: Boolean = false) {
        if (!shouldRefreshCourses && !forced) return

        shouldRefreshCourses = false

        dataSource!!.currentCourseList().asSuccess {
            currentCourses = it.data
        }.asNetEx()

        dataSource.getCardsDailyStats().asSuccess {
            flashCardDayStats = it.data
        }.asNetEx()
    }

    fun refresh() {
        dataSource!!.token ?: logout()

        viewModelScope.launch {
            isRefreshing = true

            refreshCoursePage(true)
            refreshCoursePlanPage(true)

            isRefreshing = false
        }
    }

    fun updateConnectionError(value: Boolean) {
        isConnectionError = value

        if (value) {
            preferredRoute = "courses"
            isRefreshing = false
        }
    }

    private fun <T> Result<T>.asNetEx() {
        default(this@MainPageModel)
    }
}
