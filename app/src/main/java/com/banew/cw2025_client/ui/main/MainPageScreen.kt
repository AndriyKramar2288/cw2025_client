package com.banew.cw2025_client.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.theme.AppTypography

@Composable
fun MainPageScreen(viewModel : MainPageModel) {
    LazyColumn (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x2FA5A5A5),
                        Color.Transparent
                    ),
                )
            )
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        item {
            Text(
                text = "Популярні плани навчання",
                style = AppTypography.titleMedium,
                modifier = Modifier.padding(5.dp)
            )
        }
        items(viewModel.currentCoursePlans) { item ->
            Button (
                contentPadding = PaddingValues(horizontal = 10.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .shadow(
                        4.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    )
                    .padding(7.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = Color.Transparent,
                ),
                onClick = {
                    viewModel.preferredRoute = "coursePlan/${item.id}"
                }
            ) {
                Column (modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = item.name,
                        style = AppTypography.titleLarge
                    )
                    Text(
                        text = item.author.username,
                        style = AppTypography.bodyMedium
                    )
                    Text(
                        text = item.description,
                        style = AppTypography.bodyMedium
                    )
                    Text(
                        text = item.topics.joinToString(separator = ", ") { topic -> topic.name },
                        style = AppTypography.bodyMedium
                    )
                }
            }
        }
        item {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                colorResource(R.color.navbar_button),
                                colorResource(R.color.navbar_button2)
                            )
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 20.dp)
            ) {
                Text(
                    style = AppTypography.bodyMedium,
                    text = "Не знайшли бажаний курс?\nCтворіть власний!",
                    color = colorResource(R.color.navbar_back)
                )
                Button(
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Transparent,
                        containerColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .background(
                            Color.LightGray, shape = RoundedCornerShape(5.dp)
                        ),
                    onClick = {
                        viewModel.preferredRoute = "coursePlanCreationRoute"
                    }
                ) {
                    Text(
                        text = "+",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            HorizontalDivider(
                color = colorResource(R.color.navbar_button),
                thickness = 2.dp
            )
            Spacer(Modifier.height(50.dp))
        }
    }
}