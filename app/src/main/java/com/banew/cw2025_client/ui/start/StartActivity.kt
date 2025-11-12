package com.banew.cw2025_client.ui.start

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.banew.cw2025_client.ui.main.MainActivity
import com.banew.cw2025_client.ui.theme.MyAppTheme

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyAppTheme {
                Scaffold { paddingValues ->
                    Greeting(paddingValues)
                }
            }
        }
    }
}

@Composable
fun Greeting(paddingValues: PaddingValues) {
    val context = LocalContext.current

    val navController = rememberNavController()

    Column (Modifier
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    Color.White,
                    Color(0xFFF5F5F5),
                    Color(0xFFF5F5F5),
                    Color(0xFFF5F5F5),
                    Color(0xFFF5F5F5),
                    Color.White
                )
            )
        )
        .padding(paddingValues)
        .padding(vertical = 30.dp)) {
        NavHost(
            navController = navController,
            startDestination = "gret1"
        ) {
            composable("gret1") { GreetingsStep1 {
              navController.navigate("gret2")
            } }
            composable("gret2") { GreetingsStep2 {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                (context as? Activity)?.finish()
            } }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAppTheme {
        Greeting(PaddingValues(10.dp))
    }
}