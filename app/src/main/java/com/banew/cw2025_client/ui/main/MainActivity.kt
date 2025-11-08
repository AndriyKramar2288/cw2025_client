package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.greetings.GreetingsActivity
import com.banew.cw2025_client.ui.theme.AppTypography
import com.banew.cw2025_client.ui.theme.MyAppTheme
import com.banew.cw2025_client.ui.theme.StandardFont

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()

        setContent {
            MainScreen()
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Content() {
    MainScreen(viewModel = MainPageModel(true))
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Content2() {
    CourseScreen(viewModel = MainPageModel(true))
}

@Composable
fun MainScreen(viewModel : MainPageModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var isRefreshing by remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (viewModel.isShouldToSwitchToLogin) {
        val intent = Intent(context, GreetingsActivity::class.java)
        context.startActivity(intent)
    }

    LaunchedEffect(viewModel.lastException.value) {
        viewModel.lastException.value?.let { e ->
            Toast.makeText(context, e.message ?: "Помилка", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(viewModel.preferredRoute.value) {
        navController.navigate(viewModel.preferredRoute.value)
    }

    MyAppTheme {
        Scaffold(
            bottomBar = {
                NavigationBar (
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
                        .background(Color.White)
                        .padding( horizontal = 7.dp)
                        .padding(bottom = 7.dp)
                        .shadow(8.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    colorResource(R.color.navbar_back),
                                    colorResource(R.color.navbar_back2)
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    MyNavigationItem(
                        "home",
                        currentRoute,
                        R.drawable.globe_24px,
                        "Головна",
                        viewModel
                    )
                    MyNavigationItem(
                        "courses",
                        currentRoute,
                        R.drawable.book_2_24px,
                        "Мої курси",
                        viewModel
                    )
                    MyNavigationItem(
                        "profile",
                        currentRoute,
                        R.drawable.contacts_product_24px,
                        "Профіль",
                        viewModel
                    )
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") { MainPageScreen(viewModel) }
                        composable("courses") { CourseScreen(viewModel) }
                        composable("profile") { ProfilePageScreen() }
                        composable("coursePlanCreationRoute") { CoursePlanCreationComponent(viewModel) }
                        composable(
                            route = "coursePlan/{courseId}",
                            arguments = listOf(navArgument("courseId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            backStackEntry.arguments?.getLong("courseId")?.let {
                                CoursePlanInfo(it, viewModel)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.White
                                )
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.MyNavigationItem(
    route: String,
    currentRoute: String?,
    iconRes: Int,
    label: String,
    viewModel: MainPageModel
) {
    NavigationBarItem(
        selected = currentRoute == route,
        onClick = { viewModel.preferredRoute.value = route },
        icon = {
            Icon(
                painterResource(iconRes),
                contentDescription = null,
                tint = colorResource(R.color.navbar_icon_tint),
                modifier = Modifier.size(30.dp)
            )
        },
        colors = NavigationBarItemColors(
            Color(0, 0, 0, 0),
            colorResource(R.color.navbar_text),
            Color(0, 0, 0, 0),
            colorResource(R.color.navbar_icon_tint),
            colorResource(R.color.navbar_text),
            Color(0, 0, 0, 0),
            Color(0, 0, 0, 0),
        ),
        label = { Text(text = label, fontSize = 14.sp) }
    )
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
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        item {
            Text(
                text = "Популярні плани навчання",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(5.dp)
            )
        }
        items(viewModel.currentCoursePlans.value) { item ->
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
                    viewModel.preferredRoute.value = "coursePlan/${item.id}"
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
                    fontFamily = StandardFont,
                    textAlign = TextAlign.Center,
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
                        viewModel.preferredRoute.value = "coursePlanCreationRoute"
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
        }
    }
}

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
                        colorResource(R.color.navbar_button).copy(alpha = 0.75f),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 1500f)
                )
            )
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        item {
            Text("Курси")
        }
        items(viewModel.currentCourses.value) { course ->
            Text(text = course.coursePlan.name)
        }
    }
}

@Composable
fun ProfilePageScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Профіль")
    }
}