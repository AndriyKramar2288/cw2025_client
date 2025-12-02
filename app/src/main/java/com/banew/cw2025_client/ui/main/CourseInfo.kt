package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.courses.CompendiumStatus
import com.banew.cw2025_backend_common.dto.courses.CourseDetailedDto
import com.banew.cw2025_backend_common.dto.courses.CoursePlanCourseDto
import com.banew.cw2025_backend_common.dto.courses.TopicCompendiumDto
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.R
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.ui.components.BackgroundPhotoDisplayer
import com.banew.cw2025_client.ui.components.UserProfileCard
import com.banew.cw2025_client.ui.theme.AppTypography
import kotlinx.coroutines.launch
import java.time.Instant

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Preview() {
    CourseInfo(1603L, MainPageModel(true), CourseViewModel(true))
}

class CourseViewModel(isMock: Boolean = false): ViewModel() {
    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource

    var course by mutableStateOf(
        if (!isMock) null else CourseDetailedDto(
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
                "Desc for the first normal course",
                null
            ),
            listOf(
                TopicCompendiumDto(
                    652L,
                    "НОТАТКИ",
                    CoursePlanBasicDto.TopicBasicDto(
                        1703L,
                        "ТЕМА",
                        "ОПИС ТЕМИ"
                    ),
                    listOf(
                        TopicCompendiumDto.ConceptBasicDto(
                            null,
                            "КОНЦЕПТ",
                            "ОПИС",
                            false
                        )
                    ),
                    CompendiumStatus.CURRENT
                ),
                TopicCompendiumDto(
                    653L,
                    null,
                    CoursePlanBasicDto.TopicBasicDto(
                        1704L,
                        "Topic 2",
                        "Desc 2"
                    ),
                    listOf(
                        TopicCompendiumDto.ConceptBasicDto(
                            null,
                            "ШЕ КОНЦЕПТ",
                            "ШЕ ОПИС",
                            false
                        )
                    ),
                    CompendiumStatus.COMPLETED
                )
            ),
            null
        )
    )
        private set

    fun init(coursePlanId: Long, contextModel: MainPageModel) {
        dataSource?.let { dataSource ->
            viewModelScope.launch {
                contextModel.isRefreshing = true
                dataSource.courseDetailedById(coursePlanId).asSuccess {
                    course = it.data
                }.default(contextModel)
                contextModel.isRefreshing = false
            }
        }
    }

    fun updateCompendium(updatedCompendium: TopicCompendiumDto, contextModel: MainPageModel) {
        course!!.let { crs ->
            dataSource?.let { dataSource ->
                viewModelScope.launch {
                    dataSource
                        .updateCompendium(updatedCompendium, crs.coursePlan.id)
                        .asSuccess { res ->
                            val resSuccess = res.data

                            course = CourseDetailedDto(
                                crs.id, crs.startedAt, crs.coursePlan,
                                crs.compendiums.map {
                                    if (it.topic.id != resSuccess.topic.id) it
                                    else resSuccess
                                },
                                crs.currentCompendiumId
                            )

                            contextModel.shouldRefreshCourses = true
                        }.default(contextModel)
                }
            }
        }
    }

    fun beginTopic(topicId: Long, contextModel: MainPageModel) {
        dataSource?.let { dataSource ->
            viewModelScope.launch {
                dataSource.beginTopic(topicId, course!!.coursePlan.id).asSuccess {
                    init(course!!.coursePlan.id, contextModel)
                    contextModel.preferredRoute = "compendium/${topicId}"
                    contextModel.shouldRefreshCourses = true
                }.default(contextModel)
            }
        }
    }

    fun endCourse(coursePlanId: Long, contextModel: MainPageModel) {
        dataSource?.let { dataSource ->
            viewModelScope.launch {
                dataSource.endCourse(coursePlanId).asSuccess {
                    course = it.data
                    contextModel.preferredRoute = "course/${coursePlanId}"
                    contextModel.shouldRefreshCourses = true
                }.default(contextModel)
            }
        }
    }
}

@Composable
fun CourseInfo(id: Long, viewModel: MainPageModel, courseModel: CourseViewModel) {
    val verticalScroll = rememberScrollState()

    LaunchedEffect(id) {
        courseModel.init(id, viewModel)
    }

    courseModel.course?.let { course ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScroll)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackgroundPhotoDisplayer(course.coursePlan.backgroundSrc) {
                Spacer(modifier = Modifier.height(30.dp))

                // Назва курсу
                Text(
                    text = course.coursePlan.name,
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Автор курсу
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.course_info_author),
                    style = AppTypography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                )
                UserProfileCard(
                    course.coursePlan.author,
                    viewModel,
                    Modifier.padding(horizontal = 20.dp)
                )
            }


            // Статистика курсу
            Spacer(modifier = Modifier.height(8.dp))
            CourseStats(course)

            Spacer(modifier = Modifier.height(24.dp))

            // Прогрес тем
            Text(
                text = stringResource(R.string.course_info_study_progress),
                style = AppTypography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 10.dp)
            )

            // Теми з прогресом
            course.compendiums.forEachIndexed { index, compendium ->
                TopicProgressCard(
                    compendium = compendium,
                    type = compendium.status?.toProgressType() ?: TopicProgressType.LOCKED,
                    courseModel, viewModel
                )

                // Сполучна лінія між темами
                if (index < course.compendiums.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(24.dp)
                            .background(
                                Color.LightGray,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Дата початку
            Text(
                text = stringResource(
                    R.string.course_info_started_at,
                    formatDate(course.startedAt)
                ),
                style = AppTypography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun CourseStats(course: CourseDetailedDto) {
    val totalTopics = course.compendiums.size

    val completedTopics = course.compendiums.count { it.status == CompendiumStatus.COMPLETED }
    val totalConcepts = course.compendiums.sumOf { it.concepts.size }
    val progress = if (totalTopics > 0) completedTopics.toFloat() / totalTopics else 0f

    HorizontalDivider(
        thickness = 2.dp,
        color = colorResource(R.color.navbar_button2)
    )
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.navbar_button)
        ),
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Прогрес-бар
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.course_info_basic_progress),
                    style = AppTypography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(3.dp)
                        ),
                    color = Color(0xFF4CAF50),
                    trackColor = Color.LightGray
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = AppTypography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Статистика
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = stringResource(R.string.course_info_ended_topics_count_label),
                    value = "$completedTopics/$totalTopics",
                    icon = R.drawable.fact_check_40px
                )

                StatItem(
                    label = stringResource(R.string.course_info_concepts_count_label),
                    value = totalConcepts.toString(),
                    icon = R.drawable.award_star_40px
                )
            }
        }
    }
    HorizontalDivider(
        thickness = 2.dp,
        color = colorResource(R.color.navbar_button2)
    )
}

@Composable
fun StatItem(label: String, value: String, icon: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "statItemIcon",
            Modifier
                .background(
                    shape = RoundedCornerShape(3.dp),
                    color = Color(0x07000000)
                )
                .padding(7.dp)
                .requiredSize(30.dp),
            tint = Color.White
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(2.dp)
                .background(
                    shape = RoundedCornerShape(3.dp),
                    color = Color(0x11000000)
                )
                .padding(5.dp)
        ) {
            Text(
                text = value,
                style = AppTypography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                style = AppTypography.bodySmall,
                color = Color.LightGray
            )
        }
        HorizontalDivider(
            color = Color.LightGray,
            thickness = 4.dp,
            modifier = Modifier
                .requiredWidth(50.dp)
        )
    }
}

enum class TopicProgressType(
    val backgroundColor: Color,
    val borderColor: Color,
    val buttonColor: Color,
    val buttonIconId: Int,
    val elavulationSize: Dp = 2.dp,
    val borderWidth: Dp = 1.dp,
    val isCurrent: Boolean = false,
    val isLocked: Boolean = false
) {
    LOCKED(
        backgroundColor = Color(0xFFBDBDBD), // світло-сірий
        borderColor = Color(0xFF9E9E9E),                        // середньо-сірий
        buttonColor = Color(0xFF9E9E9E),                        // сірий
        buttonIconId = R.drawable.lock_40px,
        isLocked = true
    ),

    CAN_START(
        backgroundColor = Color(0x774CAF50), // зеленкуватий
        borderColor = Color(0xFF4CAF50),                        // насичений зелений
        buttonColor = Color(0xFF4CAF50),                        // зелений
        buttonIconId = R.drawable.new_label_40px
    ),

    COMPLETED(
        backgroundColor = Color(0xC18D6E63), // м’який коричневий
        borderColor = Color(0xFF6D4C41),                        // темніший коричневий
        buttonColor = Color(0xFF8D6E63),                        // теплий коричневий
        buttonIconId = R.drawable.all_match_40px
    ),

    CURRENT(
        backgroundColor = Color(0xFFB0BEC5), // сіро-блакитний
        borderColor = Color(0xFF607D8B),                        // холодний сірий
        buttonColor = Color(0xFF607D8B),                        // той самий
        buttonIconId = R.drawable.label_24px,
        elavulationSize = 6.dp,
        borderWidth = 3.dp,
        isCurrent = true
    );
}

fun CompendiumStatus.toProgressType() = when (this) {
    CompendiumStatus.COMPLETED -> TopicProgressType.COMPLETED
    CompendiumStatus.CAN_START -> TopicProgressType.CAN_START
    CompendiumStatus.LOCKED -> TopicProgressType.LOCKED
    CompendiumStatus.CURRENT -> TopicProgressType.CURRENT
}

@Composable
fun TopicProgressCard(
    compendium: TopicCompendiumDto,
    type: TopicProgressType,
    courseModel: CourseViewModel,
    contextModel: MainPageModel
) {
    Card(
        shape = RoundedCornerShape(7.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = type.elavulationSize
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(
                width = type.borderWidth,
                color = type.borderColor,
                shape = RoundedCornerShape(7.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(type.backgroundColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row (Modifier
                .weight(5f)
                .padding(16.dp)) {
                // Інформація про тему
                Column {
                    Text(
                        text = compendium.topic.name,
                        style = AppTypography.titleSmall,
                        fontWeight = if (type.isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (type.isLocked) Color.Gray else Color.Black
                    )

                    if (!compendium.topic.description.isNullOrBlank()) {
                        Text(
                            text = compendium.topic.description,
                            style = AppTypography.bodySmall,
                            color = if (type.isLocked) Color.LightGray else Color.DarkGray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Кількість концептів
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.course_info_concepts_count,
                                compendium.concepts.size
                            ),
                            style = AppTypography.bodySmall,
                            color = if (type.isLocked) Color.LightGray else Color.Gray
                        )
                    }

                    // Нотатки (якщо є)
                    if (!compendium.notes.isNullOrBlank() && !type.isLocked) {
                        Text(
                            text = stringResource(R.string.course_info_notes_is_present),
                            style = AppTypography.bodySmall,
                            color = Color(0xFF2196F3),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Button(
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Transparent,
                    containerColor = type.buttonColor,
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = {
                    when (type) {
                        TopicProgressType.CAN_START ->
                            courseModel.beginTopic(compendium.topic.id, contextModel)
                        TopicProgressType.CURRENT -> {
                            contextModel.preferredRoute = "compendium/${compendium.topic.id}"
                        }
                        TopicProgressType.COMPLETED -> {
                            contextModel.preferredRoute = "compendium/${compendium.topic.id}"
                        }
                        else -> {}
                    }
                }
            ) {
                Image(
                    modifier = Modifier.requiredSize(30.dp),
                    painter = painterResource(type.buttonIconId),
                    contentDescription = "Choose compendium icon"
                )
            }
        }
    }
}
