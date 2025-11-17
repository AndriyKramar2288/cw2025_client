package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.components.DeathBox
import com.banew.cw2025_client.ui.components.LoadingBox
import com.banew.cw2025_client.ui.start.StartActivity
import com.banew.cw2025_client.ui.theme.MyAppTheme

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
    MainScreen(viewModel = MainPageModelMock())
}

@Composable
fun MainScreen(viewModel : MainPageModel = viewModel<MainPageModelReal>()) {
    val navController = rememberNavController()

    val context = LocalContext.current

    if (viewModel.isShouldToSwitchToLogin.value) {
        val intent = Intent(context, StartActivity::class.java)
        context.startActivity(intent)
        return
    }

    val courseModel = viewModel<CourseViewModel>()

    LaunchedEffect(viewModel.lastException.value) {
        viewModel.lastException.value?.let { e ->
            Toast.makeText(context, e.message ?: "Помилка", Toast.LENGTH_SHORT).show()
            viewModel.lastException.value = null
        }
    }

    LaunchedEffect(viewModel.preferredRouteState.value) {
        navController.navigate(viewModel.preferredRoute)
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
                        R.drawable.globe_24px,
                        "Головна",
                        viewModel
                    )
                    MyNavigationItem(
                        "courses",
                        R.drawable.book_2_24px,
                        "Прогрес",
                        viewModel
                    )
                    MyNavigationItem(
                        "profile",
                        R.drawable.contacts_product_24px,
                        "Профіль",
                        viewModel
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues),
                contentAlignment = Alignment.BottomCenter
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") { MainPageScreen(viewModel) }
                    composable("courses") { CourseScreen(viewModel) }
                    composable("profile") { ProfilePageScreen(viewModel) }
                    composable("flashCards") { FlashCardScreen(viewModel) }
                    composable(
                        route = "profile/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        backStackEntry.arguments?.getLong("userId")?.let {
                            ProfilePageScreen(viewModel, userId = it)
                        }
                    }
                    composable("coursePlanCreationRoute") { CoursePlanCreationComponent(viewModel) }
                    composable(
                        route = "coursePlan/{courseId}",
                        arguments = listOf(navArgument("courseId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        backStackEntry.arguments?.getLong("courseId")?.let {
                            CoursePlanInfo(it, viewModel)
                        }
                    }
                    composable(
                        route = "course/{courseId}",
                        arguments = listOf(navArgument("courseId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        backStackEntry.arguments?.getLong("courseId")?.let {
                            CourseInfo(it, viewModel, courseModel)
                        }
                    }
                    composable(
                        route = "compendium/{topicId}",
                        arguments = listOf(navArgument("topicId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        backStackEntry.arguments?.getLong("topicId")?.let {
                            CompendiumScreen(it, viewModel, courseModel)
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
                if (viewModel.isRefreshing.value) {
                    LoadingBox("Завантаження...")
                }
                if (viewModel.isConnectionError.value) {
                    DeathBox("Помилка з'єднання!", "Спробувати ще раз") {
                        viewModel.isConnectionError.value = false
                        viewModel.refresh()
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.MyNavigationItem(
    route: String,
    iconRes: Int,
    label: String,
    viewModel: MainPageModel
) {
    NavigationBarItem(
        selected = viewModel.preferredRoute == route,
        onClick = { viewModel.preferredRoute = route },
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