package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banew.cw2025_backend_common.dto.courses.CourseBasicDto
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.theme.AppTypography
import java.time.Instant

@Composable
fun CourseScreen(viewModel: MainPageModel) {
    LazyColumn (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x2FA5A5A5),
                        colorResource(R.color.navbar_button).copy(alpha = 0.5f),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 1500f)
                )
            )
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        item {
            Text(
                text = "Мої курси",
                style = AppTypography.titleMedium,
                modifier = Modifier.padding(5.dp)
            )
            HorizontalDivider(
                color = colorResource(R.color.navbar_button),
                thickness = 2.dp
            )
        }
        items(viewModel.currentCourses.value) { course ->
            CourseCard(course, viewModel)
        }
    }
}

@Composable
private fun CourseCard(course: CourseBasicDto, viewModel: MainPageModel) {
    Card (
        onClick = {
            viewModel.preferredRoute.value = "course/${course.coursePlan.id}"
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column (
            modifier = Modifier.padding(16.dp)
        ) {
            // Назва курсу
            Text(
                text = course.coursePlan.name,
                style = AppTypography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Автор курсу
            Row (verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Автор: ",
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = course.coursePlan.author.username ?: "Невідомий",
                    style = AppTypography.bodyMedium
                )
            }

            // Опис курсу
            course.coursePlan.description?.let { description ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Прогрес курсу
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Тем: ${course.compendiums.size}",
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Загальна кількість концептів
                val totalConcepts = course.compendiums.sumOf { it.concepts.size }
                Text(
                    text = "Концептів: $totalConcepts",
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Поточна тема
            course.currentCompendiumId?.let { currentId ->
                val currentCompendium = course.compendiums.find { it.id == currentId }
                currentCompendium?.let { compendium ->
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Поточна тема:",
                        style = AppTypography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = compendium.topic.name,
                        style = AppTypography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Дата початку
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Розпочато: ${formatDate(course.startedAt)}",
                style = AppTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatDate(instant: Instant): String {
    val formatter = java.time.format.DateTimeFormatter
        .ofPattern("dd.MM.yyyy HH:mm")
        .withZone(java.time.ZoneId.systemDefault())
    return formatter.format(instant)
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Content2() {
    CourseScreen(viewModel = MainPageModel(true))
}