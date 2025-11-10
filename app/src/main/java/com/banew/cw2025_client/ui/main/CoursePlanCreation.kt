package com.banew.cw2025_client.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.R
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.Result
import com.banew.cw2025_client.ui.components.ErrorBox
import com.banew.cw2025_client.ui.components.LoadingBox
import com.banew.cw2025_client.ui.theme.AppTypography
import com.banew.cw2025_client.ui.theme.MyAppTheme
import kotlinx.coroutines.launch

class CoursePlanCreationViewModel(val isMock: Boolean = false): ViewModel() {

    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource
    var nameFieldValue by mutableStateOf("")
    var descFieldValue by mutableStateOf("")
    var topicList by mutableStateOf(
        if (isMock) listOf(
            DataSource.TopicForm("ХУЙ", "ЇБАТЬ")
        ).flatMap { listOf(it, it, it) } else emptyList()
    )
    var lastResult by mutableStateOf<Result<CoursePlanBasicDto>?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun createCoursePlan(contextViewModel: MainPageModel) {
        viewModelScope.launch {
            isLoading = true
            when (val planRes = dataSource!!.createCoursePlan(
                nameFieldValue, descFieldValue, topicList
            )) {
                is Result.Success ->
                    contextViewModel.confirmCoursePlanCreation(planRes.data)
                is Result.Error -> {
                    lastResult = planRes
                }
            }
            isLoading = false
        }
    }
}

@Composable
fun CoursePlanCreationComponent(contextViewModel : MainPageModel? = null) {

    val formModel = if (contextViewModel == null)
        CoursePlanCreationViewModel(true)
        else
            viewModel<CoursePlanCreationViewModel>()

    Box {
        Column (
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 50.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
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
                text = "Створення курсу",
                style = AppTypography.titleLarge,
                color = colorResource(R.color.navbar_back)
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 30.dp),
                thickness = 2.dp,
                color = colorResource(R.color.navbar_button)
            )
            CoursePlanTextField(
                formModel.nameFieldValue, "Назва...",
                { it.length !in 5..255 },
                "Назва має бути розміром 5..255 включно"
            ) {
                formModel.nameFieldValue = it
            }
            CoursePlanTextField(formModel.descFieldValue, "Опис...") {
                formModel.descFieldValue = it
            }
            Row (
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    onClick = {
                        formModel.topicList += listOf(DataSource.TopicForm())
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Transparent,
                        containerColor = Color.Transparent,
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
                    text = "Додати тему",
                    style = AppTypography.titleMedium,
                    color = Color.DarkGray
                )
            }
            formModel.topicList.forEach { item ->
                Column (
                    horizontalAlignment = Alignment.End
                ) {
                    Button(
                        onClick = {
                            formModel.topicList = formModel.topicList.filter { it != item }
                        },
                        modifier = Modifier
                            .padding(end = 15.dp, top = 5.dp)
                            .background(
                                color = colorResource(R.color.navbar_button2),
                                shape = RoundedCornerShape(5.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Transparent,
                            containerColor = Color.Transparent,
                        )
                    ) {
                        Text(text = "x", color = Color.White, style = AppTypography.titleLarge)
                    }
                    Column (
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
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
                        CoursePlanTextField(
                            item.name.value, "Назва...",
                            { it.length !in 5..255 },
                            "Назва має бути розміром 5..255 включно"
                        ) {
                            item.name.value = it
                        }
                        CoursePlanTextField(item.desc.value, "Опис...") {
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
                        text = result.asError().error.message ?: "Помилка створення!",
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                onClick = {
                    formModel.createCoursePlan(contextViewModel!!)
                },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .background(
                        shape = RoundedCornerShape(10.dp),
                        brush = Brush.horizontalGradient(
                            listOf(
                                colorResource(R.color.navbar_button),
                                colorResource(R.color.navbar_button2)
                            )
                        )
                    )
                    .padding(horizontal = 50.dp),
            ) {
                Text(
                    text = "Створити",
                    style = AppTypography.titleLarge,
                    color = colorResource(R.color.navbar_back)
                )
            }
        }
        if (formModel.isLoading) {
            LoadingBox("Створення...")
        }
    }
}

@Composable
fun CoursePlanTextField(
    field : String, label : String,
    isError: (String) -> Boolean = { false }, errorMessage: String = "",
    onChange : (String) -> Unit
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .background(
                shape = RoundedCornerShape(10.dp),
                brush = Brush.horizontalGradient(
                    listOf(
                        colorResource(R.color.navbar_back),
                        colorResource(R.color.navbar_back2)
                    )
                )
            ),
        value = field,
        onValueChange = onChange,
        label = {
            Text(text = label)
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedLabelColor = Color.Gray,
            focusedLabelColor = Color.LightGray
        )
    )
    if (field.isNotBlank() && isError(field)) {
        Text(
            textAlign = TextAlign.Center,
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = AppTypography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(
                    Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Aboba() {
    MyAppTheme {
        CoursePlanCreationComponent()
    }
}