package com.banew.cw2025_client.ui.start

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.theme.AppTypography

@Composable
fun GreetingsStep1(onClick: () -> Unit) {
    // Стани для анімацій входу
    var startAnimation by remember { mutableStateOf(false) }
    var exitAnimation by remember { mutableStateOf(false) }

    // Запустити анімацію при першому відображенні
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    // Анімовані значення для logoImage
    val imageAlpha by animateFloatAsState(
        targetValue = if (startAnimation && !exitAnimation) 1f else 0f,
        animationSpec = if (!exitAnimation) {
            tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            )
        } else {
            tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        },
        label = "imageAlpha"
    )

    val imageTranslationY by animateDpAsState(
        targetValue = if (startAnimation && !exitAnimation) 0.dp else 200.dp,
        animationSpec = tween(
            durationMillis = 700,
            delayMillis = if (!exitAnimation) 1700 else 0,
            easing = EaseOutBack // подібно до OvershootInterpolator
        ),
        label = "imageTranslationY"
    )

    // Анімовані значення для logoBlock
    val logoBlockAlpha by animateFloatAsState(
        targetValue = if (startAnimation && !exitAnimation) 1f else 0f,
        animationSpec = if (!exitAnimation) {
            tween(
                durationMillis = 1000,
                delayMillis = 1700
            )
        } else {
            tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        },
        label = "logoBlockAlpha"
    )

    // Анімовані значення для continueButton
    val buttonAlpha by animateFloatAsState(
        targetValue = if (startAnimation && !exitAnimation) 1f else 0f,
        animationSpec = if (!exitAnimation) {
            tween(
                durationMillis = 500,
                delayMillis = 2700
            )
        } else {
            tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        },
        label = "buttonAlpha",
        finishedListener = {
            // Коли анімація виходу завершилась
            if (exitAnimation && it == 0f) {
                onClick()
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Основний контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (!exitAnimation) 1f else 0f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Image
            Image(
                painter = painterResource(id = R.drawable.cw2025_logo),
                contentDescription = stringResource(R.string.greetings_app_real_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(548.dp)
                    .offset(y = imageTranslationY)
                    .alpha(imageAlpha),
                contentScale = ContentScale.Fit
            )

            // Logo Block
            Column(
                modifier = Modifier
                    .width(366.dp)
                    .offset(y = (-150).dp)
                    .alpha(logoBlockAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Text
                Text(
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.Light,
                    text = stringResource(R.string.greetings_app_real_name),
                    fontSize = 30.sp,
                    modifier = Modifier.padding(vertical = 30.dp),
                    textAlign = TextAlign.Center
                )

                // Logo Info Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(
                            color = colorResource(R.color.navbar_button),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        style = AppTypography.bodyLarge,
                        text = stringResource(R.string.greetings_app_desc),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            Button (
                onClick = {
                    exitAnimation = true
                },
                modifier = Modifier
                    .alpha(buttonAlpha)
                    .padding(horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    style = AppTypography.titleLarge,
                    text = stringResource(R.string.greetings_continue),
                    color = colorResource(R.color.navbar_button),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}