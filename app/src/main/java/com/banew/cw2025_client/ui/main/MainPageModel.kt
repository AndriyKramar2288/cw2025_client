package com.banew.cw2025_client.ui.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.courses.CompendiumStatus
import com.banew.cw2025_backend_common.dto.courses.CourseBasicDto
import com.banew.cw2025_backend_common.dto.courses.CoursePlanCourseDto
import com.banew.cw2025_backend_common.dto.courses.TopicCompendiumDto
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_backend_common.dto.users.UserProfileDetailedDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.NetworkMonitor
import com.banew.cw2025_client.data.Result
import kotlinx.coroutines.launch
import java.time.Instant

interface MainPageModel {
    val currentUser: State<UserProfileDetailedDto?>
    val currentCoursePlans: State<List<CoursePlanBasicDto>>
    val currentCourses: State<List<CourseBasicDto>>
    val lastException: MutableState<Exception?>
    val preferredRoute: MutableState<String>
    fun beginCourse(coursePLanId: Long) {}
    fun confirmCoursePlanCreation(dto: CoursePlanBasicDto) {}
    fun refresh(callback: () -> Unit = {}) {}
    fun beginTopic(topicId: Long) {}
    fun updateCompendium(newCompendium: TopicCompendiumDto) {}
    fun logout() {}
    val isShouldToSwitchToLogin: State<Boolean>
        get() = mutableStateOf(false)
    val isRefreshing: MutableState<Boolean>
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
                listOf(
                    TopicCompendiumDto(
                        652L,
                        null,
                        CoursePlanBasicDto.TopicBasicDto(
                            1703L,
                            "",
                            ""
                        ),
                        emptyList(),
                        CompendiumStatus.COMPLETED
                    ),
                    TopicCompendiumDto(
                        653L,
                        null,
                        CoursePlanBasicDto.TopicBasicDto(
                            1704L,
                            "Topic 2",
                            "Desc 2"
                        ),
                        emptyList(),
                        CompendiumStatus.COMPLETED
                    )
                ),
                null
            )
        ).flatMap { listOf(it, it, it) })
    override val lastException: MutableState<Exception?>
        get() = mutableStateOf(null)
    override val preferredRoute: MutableState<String>
        get() = mutableStateOf("home")
}

class MainPageModelReal : ViewModel(), MainPageModel {
    private val dataSource: DataSource = GlobalApplication.getInstance()!!.dataSource
    override val isRefreshing = mutableStateOf(false)
    override val currentUser = mutableStateOf<UserProfileDetailedDto?>(null)
    override val currentCoursePlans =
        mutableStateOf<List<CoursePlanBasicDto>>(ArrayList())
    override val currentCourses =
        mutableStateOf<List<CourseBasicDto>>(ArrayList())
    override val lastException = mutableStateOf<Exception?>(null)
    override val preferredRoute = mutableStateOf("home")

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
                preferredRoute.value = "courses"
            }
            else {
                lastException.value = result.asError().error
            }
        }
    }

    override fun confirmCoursePlanCreation(dto: CoursePlanBasicDto) {
        currentCoursePlans.value += listOf(dto)
        //refresh()
        preferredRoute.value = "home"
    }

    override fun refresh(callback: () -> Unit) {
        viewModelScope.launch {
            isRefreshing.value = true

            val userRes = dataSource.userProfileDetailed()
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

            isRefreshing.value = false
        }
    }

    override fun beginTopic(topicId: Long) {
        viewModelScope.launch {
            when (val planRes = dataSource.beginTopic(topicId)) {
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

    override fun updateCompendium(newCompendium: TopicCompendiumDto) {
        viewModelScope.launch {
            when (val planRes = dataSource.updateCompendium(newCompendium)) {
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

    override val isShouldToSwitchToLogin = mutableStateOf(dataSource.token == null)
}
