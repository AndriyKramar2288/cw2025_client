package com.banew.cw2025_client.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banew.cw2025_client.R
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.ui.theme.AppTypography
import com.banew.cw2025_client.ui.theme.MyAppTheme

@Composable
fun CoursePlanCreationComponent(viewModel : MainPageModel? = null) {

    var nameFieldValue by remember { mutableStateOf("") }
    var descFieldValue by remember { mutableStateOf("") }
    var topicList by remember { mutableStateOf(
        if (viewModel == null) listOf(
            DataSource.TopicForm("ХУЙ", "ЇБАТЬ")
        ).flatMap { listOf(it, it, it) } else emptyList()
    ) }

    Column (
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 50.dp),
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
        CoursePlanTextField(nameFieldValue, "Назва...") {
            nameFieldValue = it
        }
        CoursePlanTextField(descFieldValue, "Опис...") {
            descFieldValue = it
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
                    topicList = listOf(DataSource.TopicForm()) + topicList
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
        LazyColumn (
            modifier = Modifier.heightIn(
                max = 300.dp
            )
        ) {
            items(topicList) { item ->
                Column (
                    horizontalAlignment = Alignment.End
                ) {
                    Button(
                        onClick = {
                            topicList = topicList.filter { it != item }
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
                        CoursePlanTextField(item.name.value, "Назва...") {
                            item.name.value = it
                        }
                        CoursePlanTextField(item.desc.value, "Опис...") {
                            item.desc.value = it
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 10.dp),
            thickness = 2.dp,
            color = colorResource(R.color.navbar_button)
        )
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                viewModel?.createCoursePlan(
                    nameFieldValue,
                    descFieldValue,
                    topicList
                )
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
}

@Composable
fun CoursePlanTextField(field : String, label : String, onChange : (String) -> Unit) {
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
}

@Composable
@Preview(showBackground = true)
fun Aboba() {
    MyAppTheme {
        CoursePlanCreationComponent()
    }
}