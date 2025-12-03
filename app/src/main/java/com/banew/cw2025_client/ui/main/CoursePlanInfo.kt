package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.R
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.ui.components.BackgroundPhotoDisplayer
import com.banew.cw2025_client.ui.components.UserProfileCard
import com.banew.cw2025_client.ui.theme.AppTypography
import com.banew.cw2025_client.ui.theme.MyAppTheme
import kotlinx.coroutines.launch

class CoursePlanInfoViewModel(val isMock: Boolean = true) : ViewModel() {

    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource
    
    var coursePlan by mutableStateOf(
        if (!isMock) null else CoursePlanBasicDto(
            3, "Курс", UserProfileBasicDto(
                44L,
                "Користувач", "aboba@gmail.com", "qwewqweq"
            ), "wqeqeqwwq", listOf(
                CoursePlanBasicDto.TopicBasicDto(
                    null, "тема 1", "опис"
                )
            ), 4, null, true
        )
    )
        private set

    var isEdit by mutableStateOf(false)

    fun loadCourseById(id: Long, contextModel: MainPageModel) {
        dataSource?.let { dataSource ->
            viewModelScope.launch {
                contextModel.isRefreshing = true
                dataSource.loadCoursePlanById(id).asSuccess {
                    coursePlan = it.data
                }.default(contextModel)
                contextModel.isRefreshing = false
            }
        }
    }

    fun editCourse(plan: CoursePlanBasicDto) {
        isEdit = false
        coursePlan = plan
    }
}

@Composable
fun CoursePlanInfo(id: Long, contextModel: MainPageModel, viewModel: CoursePlanInfoViewModel = viewModel()) {

    LaunchedEffect(id) {
        viewModel.loadCourseById(id, contextModel)
    }

    viewModel.coursePlan?.let { coursePlan ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BackgroundPhotoDisplayer(
                    coursePlan.backgroundSrc,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    // Назва курсу
                    Text(
                        text = coursePlan.name,
                        style = AppTypography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Автор
                    UserProfileCard(coursePlan.author, contextModel)

                    // Опис курсу
                    if (!coursePlan.description.isNullOrBlank()) {
                        Text(
                            text = coursePlan.description,
                            style = AppTypography.bodyMedium,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            colorResource(R.color.navbar_button)
                                                .copy(alpha = 0.5f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp)
                        )
                    }

                    // Теми курсу
                    Text(
                        text = stringResource(R.string.course_plan_info_topics),
                        style = AppTypography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                HorizontalDivider(
                    color = colorResource(R.color.navbar_button),
                    thickness = 2.dp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    coursePlan.topics.forEach { topic ->
                        Column(modifier = Modifier
                            .background(Color.White)
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 15.dp)) {
                            Text(
                                color = Color.Black,
                                text = topic.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (!topic.description.isNullOrBlank()) {
                                Text(
                                    text = topic.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
                val isUserAdded = !contextModel.currentCourses
                    .any { it.coursePlan.id == coursePlan.id }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    CoursePlanInfoButton(
                        if (isUserAdded)
                            stringResource(R.string.course_plan_info_join)
                        else
                            stringResource(R.string.course_plan_info_goto)
                    ) {
                        if (isUserAdded)
                            contextModel.beginCourse(coursePlan.id)
                        else
                            contextModel.preferredRoute = "course/${coursePlan.id}"
                    }
                    if (coursePlan.author.id == contextModel.currentUser?.id) {
                        CoursePlanInfoButton(
                            stringResource(R.string.course_plan_info_edit_course_button)
                        ) {
                            viewModel.isEdit = true
                        }
                    }
                }
                Spacer(Modifier.height(50.dp))
            }
            if (viewModel.isEdit) {
                CoursePlanModifyingForm(
                    contextModel, viewModel.coursePlan,
                    {
                        viewModel.editCourse(it)
                    },
                    {
                        viewModel.isEdit = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CoursePlanInfoButton(text: String, onClick: () -> Unit) {
    Button(
        contentPadding = PaddingValues(horizontal = 15.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Transparent,
            containerColor = Color.Gray.copy(alpha = 0.8f),
            disabledContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(5.dp),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = AppTypography.labelMedium,
            color = Color.White
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Aboba() {
    MyAppTheme {
        CoursePlanInfo(3, MainPageModel(true), CoursePlanInfoViewModel(true))
    }
}