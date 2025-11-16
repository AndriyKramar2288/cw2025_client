package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banew.cw2025_backend_common.dto.courses.CourseBasicDto
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.components.PagerIndicator
import com.banew.cw2025_client.ui.theme.AppTypography
import java.time.Instant

@Composable
fun CourseScreen(viewModel: MainPageModel) {

    val pagerState = rememberPagerState { viewModel.currentCourses.value.size }

    Column (
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
            .padding(vertical = 15.dp)
    ) {
        Text(
            text = "Мої курси",
            style = AppTypography.titleMedium,
            modifier = Modifier.padding(5.dp)
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 20.dp),
            color = colorResource(R.color.navbar_button),
            thickness = 2.dp
        )
        PagerIndicator(
            pagerState.currentPage,
            viewModel.currentCourses.value.size
        )
        HorizontalPager(
            state = pagerState
        ) {
            CourseCard(viewModel.currentCourses.value[it], viewModel)
        }
    }
}

@Composable
private fun CourseCard(course: CourseBasicDto, viewModel: MainPageModel) {
    Card (
        shape = RectangleShape,
        onClick = {
            viewModel.preferredRoute.value = "course/${course.coursePlan.id}"
        },
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        HorizontalDivider(
            thickness = 2.dp,
            color = colorResource(R.color.navbar_button2)
        )
        // Назва курсу
        Text(
            modifier = Modifier
                .padding(top = 3.dp)
                .fillMaxWidth()
                .background(colorResource(R.color.navbar_button).copy(alpha = 0.75f))
                .padding(7.dp),
            text = course.coursePlan.name,
            style = AppTypography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Column (
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color.White,
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp)
        ) {
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
                ProgressBlock(
                    text = "Тем: ${course.totalTopics}",
                    iconId = R.drawable.fact_check_40px
                )

                ProgressBlock(
                    text = "Концептів: ${course.totalConcepts}",
                    iconId = R.drawable.award_star_40px
                )
            }

            // Поточна тема
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Поточна тема:",
                style = AppTypography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = course.currentTopic,
                style = AppTypography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

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

@Composable
private fun ProgressBlock(text: String, iconId: Int) {
    Row (
        modifier = Modifier
            .border(
                1.dp, colorResource(R.color.navbar_button),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = "statItemIcon",
            Modifier
                .requiredSize(30.dp)
                .background(
                    shape = RoundedCornerShape(3.dp),
                    color = colorResource(R.color.navbar_button).copy(alpha = 0.5f)
                )
                .padding(7.dp),
            tint = Color.White
        )
        Text(
            modifier = Modifier
                .requiredHeight(30.dp)
                .padding(start = 3.dp)
                .background(
                    shape = RoundedCornerShape(3.dp),
                    color = colorResource(R.color.navbar_button2).copy(alpha = 0.5f)
                )
                .padding(vertical = 5.dp, horizontal = 10.dp),
            text = text,
            style = AppTypography.bodyMedium,
            color = Color.Black.copy(alpha = 0.75f),
            fontSize = 15.sp
        )
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
    CourseScreen(viewModel = MainPageModelMock())
}