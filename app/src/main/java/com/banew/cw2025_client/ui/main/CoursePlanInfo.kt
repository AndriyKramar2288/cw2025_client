package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.theme.AppTypography
import com.banew.cw2025_client.ui.theme.MyAppTheme

@Composable
fun CoursePlanInfo(id: Long, viewModel: MainPageModel) {
    val verticalScroll = rememberScrollState()

    // Отримуємо курс за ID
    val coursePlan by remember {
        mutableStateOf(
            viewModel.currentCoursePlans.value.first { it.id == id }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)
            .padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
        UserProfileCard(coursePlan.author)

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
                                colorResource(R.color.navbar_button).copy(alpha = 0.1f),
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
            text = "Теми курсу",
            style = AppTypography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            coursePlan.topics.forEach { topic ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
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
        }
        Button(
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Transparent,
                containerColor = Color.Transparent,
            ),
            modifier = Modifier
                .padding(start = 20.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Gray, Color.Gray.copy(alpha = 0.8f))
                    ),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding( horizontal = 30.dp),
            onClick = {
                viewModel.beginCourse(coursePlan.id)
            }
        ) {
            Text(
                text = "Доєднатись до курсу",
                style = AppTypography.labelMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun UserProfileCard(userProfile: UserProfileBasicDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(R.color.navbar_back),
                        colorResource(R.color.navbar_button2).copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Transparent,
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage( // coil-compose
                model = userProfile.photoSrc ?: "",
                contentDescription = "Автор",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = userProfile.username,
                    style = AppTypography.bodyMedium,
                )
                Text(
                    text = userProfile.email,
                    style = AppTypography.bodyMedium,
                    color = Color.Gray.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Aboba() {
    MyAppTheme {
        CoursePlanInfo(3, MainPageModelMock())
    }
}