package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.R
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.Result
import com.banew.cw2025_client.ui.components.ErrorBox
import com.banew.cw2025_client.ui.components.LoadingBox
import com.banew.cw2025_client.ui.components.MySwitch
import com.banew.cw2025_client.ui.theme.AppTypography
import com.banew.cw2025_client.ui.theme.MyAppTheme
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class CoursePlanModifyingViewModelFactory(
    val isMock: Boolean = false,
    val prevDto: CoursePlanBasicDto? = null
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CoursePlanModifyingViewModel(isMock, prevDto) as T
    }
}

class CoursePlanModifyingViewModel(
    val isMock: Boolean,
    val prevDto: CoursePlanBasicDto?
): ViewModel() {

    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource
    var nameFieldValue by mutableStateOf("")
    var descFieldValue by mutableStateOf("")
    var backFieldValue by mutableStateOf("")
    var isPublicValue by mutableStateOf(true)
    var topicList by mutableStateOf(
        if (isMock) listOf(
            DataSource.TopicForm("TOPIC", "DESC")
        ).flatMap { listOf(it, it, it) } else emptyList()
    )
    var lastResult by mutableStateOf<Result<CoursePlanBasicDto>?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadFromPrev()
    }

    private fun loadFromPrev() {
        prevDto?.let {
            nameFieldValue = it.name
            descFieldValue = it.description ?: ""
            backFieldValue = it.backgroundSrc ?: ""
            isPublicValue = it.isPublic
            topicList = it.topics.map { t -> DataSource.TopicForm(t.name, t.description) }
        }
    }

    private fun toBasicDto() = CoursePlanBasicDto(
        prevDto?.id,
        nameFieldValue,
        prevDto?.author,
        descFieldValue,
        topicList.mapIndexed { index, form ->
            CoursePlanBasicDto.TopicBasicDto(
                prevDto?.topics?.getOrNull(index)?.id,
                form.name.value, form.desc.value
            )
        },
        prevDto?.studentCount ?: 0,
        backFieldValue.ifBlank { null },
        isPublicValue
    )

    fun createCoursePlan(contextViewModel: MainPageModel) {
        viewModelScope.launch {
            isLoading = true
            dataSource!!.createCoursePlan(toBasicDto()).asSuccess {
                contextViewModel.shouldRefreshCoursePlans = true
                contextViewModel.preferredRoute = "home"
            }.default(contextViewModel)
            isLoading = false
        }
    }

    fun updateCoursePlan(contextViewModel: MainPageModel, successCallback: (CoursePlanBasicDto) -> Unit) {
        prevDto?.let {
            viewModelScope.launch {
                isLoading = true
                dataSource!!.updateCoursePlan(toBasicDto()).asSuccess {
                    successCallback(it.data)
                    contextViewModel.shouldRefreshCoursePlans = true
                    contextViewModel.shouldRefreshCourses = true
                }.default(contextViewModel)
                isLoading = false
            }
        }
    }

    fun cancel() {
        loadFromPrev()
    }
}

@Composable
fun CoursePlanCreationComponent(
    contextViewModel: MainPageModel,
) {
    CoursePlanModifyingForm(contextViewModel, null)
}

@Composable
fun CoursePlanModifyingForm(
    contextViewModel: MainPageModel,
    prevDto: CoursePlanBasicDto?,
    successCallback: (CoursePlanBasicDto) -> Unit = {},
    cancelCallback: () -> Unit = {}
) {
    val formModel = viewModel<CoursePlanModifyingViewModel>(
        factory = CoursePlanModifyingViewModelFactory(false, prevDto)
    )

    val isUpdate by remember { mutableStateOf(formModel.prevDto != null) }

    Box(
        Modifier
            .clickable(false) {}
            .background(
                if (isUpdate)
                    Color(0xA8878787)
                else
                    Color.Transparent
            )
    ) {
        Column (
            Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(5.dp),
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.DarkGray,
                                Color(0xFF646464)
                            )
                        )
                    )
                    .padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
                text = if (isUpdate)
                    stringResource(R.string.course_plan_creation_updating_label)
                else
                    stringResource(R.string.course_plan_creation_label),
                style = AppTypography.titleMedium,
                color = colorResource(R.color.navbar_back)
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 30.dp),
                thickness = 2.dp,
                color = colorResource(R.color.navbar_button)
            )
            CompendiumTextField (
                formModel.nameFieldValue, stringResource(R.string.course_plan_creation_name_label),
                isError = { it.length !in 5..255 },
                errorMessage = stringResource(R.string.course_plan_creation_name_alert)
            ) {
                formModel.nameFieldValue = it
            }
            CompendiumTextField (formModel.descFieldValue,
                stringResource(R.string.course_plan_creation_desc_label),
                largeText = true
            ) {
                formModel.descFieldValue = it
            }
            CompendiumTextField (formModel.backFieldValue,
                stringResource(R.string.course_plan_creation_background_src)
            ) {
                formModel.backFieldValue = it
            }
            Row (
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .background(
                        colorResource(R.color.navbar_back),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MySwitch(
                    formModel.isPublicValue,
                    {
                        formModel.isPublicValue = it
                    }
                ) {
                    Icon(
                        if (formModel.isPublicValue)
                            painterResource(R.drawable.public_40px)
                        else
                            painterResource(R.drawable.public_off_40px),
                        contentDescription = "is public icon"
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.course_plan_creation_is_public),
                    style = AppTypography.bodyMedium
                )
            }
            if (!isUpdate) Row (
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        formModel.topicList += listOf(DataSource.TopicForm())
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Transparent,
                        containerColor = Color.LightGray,
                    )
                ) {
                    Text(
                        text = "+",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .background(
                            shape = RoundedCornerShape(5.dp),
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color.LightGray,
                                    colorResource(R.color.navbar_back2)
                                )
                            )
                        )
                        .padding(vertical = 10.dp, horizontal = 50.dp),
                    text = stringResource(R.string.course_plan_creation_add_topic),
                    style = AppTypography.bodyMedium,
                    color = Color.DarkGray
                )
            }
            formModel.topicList.forEach { item ->
                Column (
                    horizontalAlignment = Alignment.End
                ) {
                    if (!isUpdate) Button(
                        onClick = {
                            formModel.topicList = formModel.topicList.filter { it != item }
                        },
                        modifier = Modifier
                            .padding(end = 15.dp, top = 10.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Transparent,
                            containerColor = colorResource(R.color.navbar_button2),
                        )
                    ) {
                        Text(text = "x", color = Color.White, style = AppTypography.titleLarge)
                    }
                    Column (
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .padding(bottom = 10.dp)
                            .background(
                                shape = RoundedCornerShape(5.dp),
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        Color.LightGray,
                                        colorResource(R.color.navbar_button)
                                    )
                                )
                            )
                            .padding(horizontal = 10.dp)
                            .padding(bottom = 5.dp)
                    ) {
                        CompendiumTextField (
                            item.name.value,
                            stringResource(R.string.course_plan_creation_name_label),
                            isError = { it.length !in 5..255 },
                            errorMessage = stringResource(R.string.course_plan_creation_name_alert),
                        ) {
                            item.name.value = it
                        }
                        CompendiumTextField (
                            item.desc.value,
                            stringResource(R.string.course_plan_creation_desc_label),
                            largeText = true
                        ) {
                            item.desc.value = it
                        }
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(top = 10.dp),
                thickness = 2.dp,
                color = colorResource(R.color.navbar_button)
            )
            formModel.lastResult?.let { result ->
                if (result.isError) {
                    ErrorBox(
                        text = result.asError().error.message
                            ?: stringResource(R.string.course_plan_creation_error),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (isUpdate) {
                    FormButton(
                        stringResource(R.string.course_plan_creation_cancel), Color.Gray
                    ) {
                        cancelCallback()
                        formModel.cancel()
                    }
                }
                FormButton(
                    if (isUpdate)
                        stringResource(R.string.course_plan_creation_update)
                    else
                        stringResource(R.string.course_plan_creation_create),
                    colorResource(R.color.navbar_button)
                ) {
                    if (isUpdate)
                        formModel.updateCoursePlan(contextViewModel, successCallback)
                    else
                        formModel.createCoursePlan(contextViewModel)
                }
            }
            Spacer(Modifier.height(50.dp))
        }
        if (formModel.isLoading) {
            LoadingBox(stringResource(R.string.course_plan_creation_creating))
        }
    }
}

@Composable
private fun FormButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 40.dp),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .padding(top = 20.dp),
    ) {
        Text(
            text = text,
            style = AppTypography.titleSmall,
            color = colorResource(R.color.navbar_back)
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Aboba() {
    MyAppTheme {
        CoursePlanCreationComponent(
            MainPageModel(true)
        )
    }
}