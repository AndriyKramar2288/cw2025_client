package com.banew.cw2025_client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.main.MainPageModel
import com.banew.cw2025_client.ui.theme.AppTypography
import java.time.Duration

@Composable
@Preview(showBackground = true)
private fun LoadingPreview() {
    LoadingBox("Завантаження...")
}

@Composable
@Preview(showBackground = true)
private fun DeathPreview() {
    DeathBox("Помилка!", "Спробувати ще раz") {}
}

@Composable
@Preview(showBackground = true)
private fun AlertWrapPreview() {
    Box(Modifier.fillMaxSize()) {
        AlertDialogWrap(true, {}, {})
    }
}

@Composable
fun ErrorBox(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_alert),
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = AppTypography.bodyMedium
            )
        }
    }
}

@Composable
fun UserProfileCard(userProfile: UserProfileBasicDto, model: MainPageModel, modifier: Modifier = Modifier) {
    Card(
        onClick = {
            model.preferredRoute = "profile/${userProfile.id}"
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(R.color.navbar_back),
                        colorResource(R.color.navbar_button2).copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Transparent,
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage( // coil-compose
                model = userProfile.photoSrc ?: "",
                contentDescription = "Автор",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = userProfile.username,
                    style = AppTypography.bodyMedium,
                )
                Text(
                    text = userProfile.email,
                    style = AppTypography.bodyMedium,
                    color = Color.Gray.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun DeathBox(text1: String, text2: String, onClick: () -> Unit) {
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(
                Color.Black.copy(alpha = 0.3f),
                Color(0x8B973232),
                Color(0x8B973232),
                Color.Transparent,
            )))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        Card (
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.navbar_button2)
            )
        ) {
            Column (
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text1,
                    style = AppTypography.bodyLarge,
                    color = Color.White
                )
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text(
                        text2,
                        style = AppTypography.bodyLarge,
                        color = Color.White
                    )
                }
                HorizontalDivider(
                    Modifier
                        .padding(top = 20.dp)
                        .requiredWidth(100.dp),
                    thickness = 2.dp,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun LoadingBox(text: String) {
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(
                Color.Black.copy(alpha = 0.3f),
                Color.Black.copy(alpha = 0.3f),
                Color.Black.copy(alpha = 0.3f),
                Color.Transparent,
            )))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        Card (
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.navbar_button)
            )
        ) {
            Column (
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text,
                    style = AppTypography.bodyLarge,
                    color = Color.White
                )
                HorizontalDivider(
                    Modifier
                        .padding(top = 20.dp)
                        .requiredWidth(100.dp),
                    thickness = 2.dp,
                    color = Color.LightGray
                )
            }
        }
    }
}

fun Duration.clockFormat(): String {
    val h = toHours()
    val m = toMinutesPart()
    val s = toSecondsPart()

    return if (h > 0)
        "%d:%02d:%02d".format(h, m, s)
    else
        "%02d:%02d".format(m, s)
}

@Composable
fun PagerIndicator(current: Int, total: Int, onClick: (Int) -> Unit = {}) {
    val edgeSize = 15 // момент, на якому воно переходе з кружочків на просто

    if (total > 1) Row(
        Modifier
            .background(
                colorResource(R.color.navbar_button),
                RoundedCornerShape(2.dp)
            )
            .padding(10.dp)
    ) {
        if (total >= edgeSize) Text(
            style = AppTypography.bodySmall,
            color = Color.White,
            text = "${current + 1} з $total"
        )
        else for (i in 0 until total) {
            Card (
                onClick = {
                    onClick(i)
                },
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .requiredSize(10.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (i == current)
                        Color.LightGray
                    else
                        Color.White
                )
            ) {}
        }
    }
}

@Composable
fun AlertDialogWrap(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    message: String = "Ви впевнені, що хочете продовжити?"
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                }) { Text("Так") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()
                }) { Text("Скасувати") }
            },
            title = { Text("Підтвердження") },
            text = { Text(message) },
            shape = RoundedCornerShape(3.dp),
            textContentColor = Color.DarkGray,
            containerColor = colorResource(R.color.navbar_back)
        )
    }
}