package com.banew.cw2025_client.ui.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banew.cw2025_backend_common.dto.cards.FlashCardDayStats
import com.banew.cw2025_backend_common.dto.cards.FlashCardType
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.courses.CourseBasicDto
import com.banew.cw2025_backend_common.dto.courses.CoursePlanCourseDto
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_backend_common.dto.users.UserProfileDetailedDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.NetworkMonitor
import com.banew.cw2025_client.data.Result
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

interface MainPageModel {
    val currentUser: State<UserProfileDetailedDto?>

    val currentCoursePlans: State<List<CoursePlanBasicDto>>
    val currentCourses: State<List<CourseBasicDto>>
    val flashCardDayStats: State<FlashCardDayStats?>

    val lastException: MutableState<Exception?>

    var preferredRoute: String
    val preferredRouteState: State<String>
        get() = mutableStateOf("home")

    fun beginCourse(coursePLanId: Long) {}
    fun confirmCoursePlanCreation(dto: CoursePlanBasicDto) {}
    fun refresh(callback: () -> Unit = {}) {}
    fun logout() {}
    val isShouldToSwitchToLogin: State<Boolean>
        get() = mutableStateOf(false)
    val isRefreshing: MutableState<Boolean>
        get() = mutableStateOf(false)
    val isConnectionError: MutableState<Boolean>
        get() = mutableStateOf(false)
}

class MainPageModelMock: ViewModel(), MainPageModel {
    override val currentUser: State<UserProfileDetailedDto?>
        get() = mutableStateOf(
            UserProfileDetailedDto(
                1L,
                "Користувач", "aboba@gmail.com", "qwewqweq", listOf()
            )
        )
    override val currentCoursePlans: State<List<CoursePlanBasicDto>>
        get() = mutableStateOf(
            listOf(
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
            ).flatMap { listOf(it, it, it, it, it) }.flatMap { listOf(it, it, it) }
        )
    override val currentCourses: State<List<CourseBasicDto>>
        get() = mutableStateOf(listOf(
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
    override val flashCardDayStats: State<FlashCardDayStats?>
        get() = mutableStateOf(FlashCardDayStats(
            mapOf(
                FlashCardType.NEW to 1,
                FlashCardType.REPEAT to 2,
                FlashCardType.STUDY to 3
            ),
            10,
            Duration.ofMinutes(5)
        ))


    override val lastException: MutableState<Exception?>
        get() = mutableStateOf(null)
    override var preferredRoute = "home"
}

class MainPageModelReal : ViewModel(), MainPageModel {
    private val dataSource: DataSource = GlobalApplication.getInstance()!!.dataSource
    override val isRefreshing = mutableStateOf(false)
    override val currentUser = mutableStateOf<UserProfileDetailedDto?>(null)
    override val currentCoursePlans =
        mutableStateOf<List<CoursePlanBasicDto>>(ArrayList())
    override val currentCourses =
        mutableStateOf<List<CourseBasicDto>>(ArrayList())
    override val flashCardDayStats: MutableState<FlashCardDayStats?> =
        mutableStateOf(null)
    override val lastException = mutableStateOf<Exception?>(null)

    override var preferredRoute
        get() = preferredRouteState.value
        set(value) {
            when (value){
                "courses" -> {
                    viewModelScope.launch {
                        val dayStats = dataSource.getCardsDailyStats()
                        if (dayStats.isSuccess) flashCardDayStats.value = dayStats.asSuccess().data
                    }
                }
            }

            preferredRouteState.value = value
        }

    override var preferredRouteState = mutableStateOf("home")

    private val networkMonitor: NetworkMonitor? = NetworkMonitor.getInstance(
        GlobalApplication.getInstance().applicationContext
    )

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

    override fun logout() {
        dataSource.logout()
        isShouldToSwitchToLogin.value = true
    }

    override fun beginCourse(coursePLanId: Long) {
        viewModelScope.launch {
            val result = dataSource.beginCourse(coursePLanId)

            if (result.isSuccess) {
                currentCourses.value += listOf(result.asSuccess().data)
                preferredRoute = "courses"
            }
            else {
                lastException.value = result.asError().error
            }
        }
    }

    override fun confirmCoursePlanCreation(dto: CoursePlanBasicDto) {
        refresh()
        preferredRoute = "home"
    }

    override fun refresh(callback: () -> Unit) {
        viewModelScope.launch {
            isRefreshing.value = true

            val userRes = dataSource.userProfileDetailed()
            val plansRes = dataSource.currentCoursePlanList()
            val coursesRes = dataSource.currentCourseList()
            val dayStats = dataSource.getCardsDailyStats()

            dataSource.token ?: logout()

            when (userRes) {
                is Result.Success -> currentUser.value = userRes.data
                is Result.Error -> lastException.value = userRes.error
            }

            if (dayStats.isSuccess) flashCardDayStats.value = dayStats.asSuccess().data
            if (plansRes.isSuccess) currentCoursePlans.value = plansRes.asSuccess().data
            if (coursesRes.isSuccess) currentCourses.value = coursesRes.asSuccess().data

            isConnectionError.value = userRes.isError && plansRes.isError && coursesRes.isError

            callback()

            isRefreshing.value = false
        }
    }

    override val isShouldToSwitchToLogin = mutableStateOf(dataSource.token == null)
    override val isConnectionError = mutableStateOf(dataSource.token == null)
}
