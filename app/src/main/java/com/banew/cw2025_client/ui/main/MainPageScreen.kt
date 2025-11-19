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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.theme.AppTypography

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Content() {
    MainPageScreen(viewModel = MainPageModel(true))
}

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
            .padding(vertical = 15.dp)
    ) {
        item {
            Text(
                text = "Популярні плани навчання",
                style = AppTypography.titleMedium,
                modifier = Modifier.padding(5.dp)
            )
            SearchRow(viewModel)
        }
        items(viewModel.currentCoursePlans) { item ->
            Card (
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                onClick = {
                    viewModel.preferredRoute = "coursePlan/${item.id}"
                }
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (modifier = Modifier.weight(1f).padding(10.dp)) {
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
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painterResource(R.drawable.fact_check_40px),
                            contentDescription = "topics icon",
                            tint = Color.LightGray,
                            modifier = Modifier.requiredSize(30.dp)
                        )
                        Text(
                            "Тем: ${item.topics.size}",
                            style = AppTypography.bodySmall
                        )
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(
                color = colorResource(R.color.navbar_button),
                thickness = 3.dp
            )
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 35.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                colorResource(R.color.navbar_button),
                                colorResource(R.color.navbar_button2)
                            )
                        ),
                    )
                    .padding(horizontal = 10.dp, vertical = 20.dp)
            ) {
                Text(
                    style = AppTypography.bodyMedium,
                    text = "Не знайшли бажаний курс?\nCтворіть власний!",
                    color = colorResource(R.color.navbar_back)
                )
                IconButton (
                    onClick = {
                        viewModel.preferredRoute = "coursePlanCreationRoute"
                    },
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(48.dp)
                        .background(
                            Color.Gray,
                            shape = RoundedCornerShape(5.dp)
                        )
                ) {
                    Icon(
                        painterResource(R.drawable.library_add_40px),
                        tint = Color.LightGray,
                        modifier = Modifier.requiredSize(30.dp),
                        contentDescription = "Create course plan icon"
                    )
                }
            }
            HorizontalDivider(
                color = colorResource(R.color.navbar_button),
                thickness = 3.dp
            )
            Spacer(Modifier.height(50.dp))
        }
    }
}

@Composable
fun SearchRow(viewModel: MainPageModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(colorResource(R.color.navbar_button))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            modifier = Modifier
                .weight(1f),
            placeholder = { Text(
                "Пошук...", style = AppTypography.bodySmall
            ) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFEFEFEF),
                focusedContainerColor = Color(0xFFF8F8F8),
                unfocusedIndicatorColor = Color.LightGray,
                focusedIndicatorColor = Color.Gray,
            ),
            textStyle = AppTypography.bodyMedium,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { viewModel.searchCoursePlans() }
            )
        )
        IconButton (
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.LightGray
            ),
            onClick = { viewModel.searchCoursePlans() },
            modifier = Modifier
                .size(48.dp)
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            Icon(
                painterResource(R.drawable.find_in_page_40px),
                tint = Color.White,
                modifier = Modifier.requiredSize(30.dp),
                contentDescription = "Search icon"
            )
        }
    }
}