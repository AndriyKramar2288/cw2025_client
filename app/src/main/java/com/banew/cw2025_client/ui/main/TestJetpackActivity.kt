package com.banew.cw2025_client.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banew.cw2025_client.ui.main.ui.theme.Cw2025_clientTheme

class TestJetpackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(
                content = { paddingValues ->
                    Content(paddingValues)
                }
            )
        }
    }
}

@Composable
private fun Content(paddingValues: PaddingValues) {
    Column {
        Text(
            text = "Test!!",
            fontSize = 30.sp,
            modifier = Modifier.padding(paddingValues)
        )
        Text(
            text = "Test!!",
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ContentPreview() {
    Content(PaddingValues())
}