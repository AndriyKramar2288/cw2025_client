package com.banew.cw2025_client.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
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

@Composable
@Preview(showBackground = true)
private fun Content() {
    MainScreen()
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
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Головна") }
                )
                NavigationBarItem(
                    selected = currentRoute == "courses",
                    onClick = { navController.navigate("courses") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Курси") }
                )
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Профіль") }
                )
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