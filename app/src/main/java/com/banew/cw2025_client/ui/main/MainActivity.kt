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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.greetings.GreetingsActivity

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

    LaunchedEffect(viewModel.lastException) {
        viewModel.lastException.value?.let { e ->
            Toast.makeText(context, e.message ?: "Помилка", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            Column (
                modifier = Modifier.padding(7.dp)
            ) {
                NavigationBar (
                    containerColor = Color.Transparent,
                    modifier = Modifier
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
                        navController
                    )
                    MyNavigationItem(
                        "courses",
                        currentRoute,
                        R.drawable.book_2_24px,
                        "Курси",
                        navController
                    )
                    MyNavigationItem(
                        "profile",
                        currentRoute,
                        R.drawable.contacts_product_24px,
                        "Профіль",
                        navController
                    )
                }
            }
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier.padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { MainPageScreen(viewModel.currentUser) }
                composable("courses") { CreateCoursePlanScreen() }
                composable("profile") { ProfilePageScreen() }
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
    navController: NavHostController
) {
    NavigationBarItem(
        selected = currentRoute == route,
        onClick = { navController.navigate(route) },
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
fun MainPageScreen(text: State<UserProfileBasicDto?>) {

    val user = text.value

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Ім'я - ${user?.username}")
    }
}

@Composable
fun CreateCoursePlanScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Курси")
    }
}

@Composable
fun ProfilePageScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Профіль")
    }
}