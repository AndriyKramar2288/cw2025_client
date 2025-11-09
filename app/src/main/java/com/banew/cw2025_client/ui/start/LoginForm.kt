package com.banew.cw2025_client.ui.start

import android.util.Patterns
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.theme.AppTypography

@Composable
fun GreetingsStep2(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {

    // Стани для анімацій
    var startAnimation by remember { mutableStateOf(false) }

    var isLogin by remember { mutableStateOf(true) }

    // Запуск анімації при завантаженні
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    // Обробка успішного логіну
    LaunchedEffect(viewModel.loginResult) {
        viewModel.loginResult?.let { result ->
            if (result.isSuccess) {
                onLoginSuccess()
            }
        }
    }

    // Анімації появи
    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "contentAlpha"
    )

    val contentOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 50.dp,
        animationSpec = tween(
            durationMillis = 600,
            easing = EaseOutCubic
        ),
        label = "contentOffsetY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F5F5),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
                .offset(y = contentOffsetY)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Вітаємо!",
                    style = AppTypography.headlineLarge,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            colorResource(R.color.navbar_button).copy(alpha = 0.7f)
                        ).padding(10.dp),
                    textAlign = TextAlign.Center,
                    text = "Увійдіть до свого акаунту",
                    style = AppTypography.bodyLarge,
                    color = Color.White
                )
            }
            Row(
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                SwitchButton(
                    text = "Увійти",
                    isEnabled = !isLogin,
                ) {
                    isLogin = true
                }
                SwitchButton(
                    text = "Зареєструватись",
                    isEnabled = isLogin,
                ) {
                    isLogin = false
                }
            }

            // Email Field
            FormField(
                "Електронна скринька",
                "example@email.com",
                android.R.drawable.ic_dialog_email,
                viewModel.email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(
                    viewModel.email
                ).matches(),
                "Введіть коректну email адресу",
                viewModel.email
            ) { viewModel.email = it }

            if (!isLogin) {
                // Username Field
                FormField(
                    "Ім'я користувача",
                    "Введіть псевдонім...",
                    android.R.drawable.star_on,
                    viewModel.username.isNotEmpty() && viewModel.username.length <= 5,
                    "Псевдо має бути не менше 5 символів",
                    viewModel.username
                ) { viewModel.username = it }
                // Photo Field
                FormField(
                    "Посилання на аватар користувача",
                    "Вставте посилання на фото...",
                    android.R.drawable.ic_menu_mapmode,
                    false,
                    ":)",
                    viewModel.photoSrc
                ) { viewModel.photoSrc = it }

                AsyncImage( // coil-compose
                    model = viewModel.photoSrc,
                    contentDescription = "Фото",
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            }

            // Password Field
            FormField(
                "Пароль",
                "Введіть пароль",
                android.R.drawable.ic_lock_idle_lock,
                viewModel.password.isNotEmpty() && viewModel.password.length < 8,
                "Пароль повинен містити більше 8 символів",
                viewModel.password,
                isPassword = true,
            ) { viewModel.password = it }

            Spacer(modifier = Modifier.height(8.dp))

            // Login Error Message
            viewModel.loginResult?.let { result ->
                if (result.isError) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
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
                                text = "Невірний email або пароль",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = AppTypography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Login Button
            Button(
                onClick = {
                    if (isLogin)
                        viewModel.login()
                    else
                        viewModel.register()
                },
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3E2F2F),
                    disabledContainerColor = Color.LightGray
                ),
                enabled = isLoginEnabled(viewModel, isLogin) && !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isLogin) "Увійти" else "Зареєструватись",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Loading Overlay
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                Card (
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column (
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Вхід...",
                            style = AppTypography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormField(
    text: String, placeholder: String,
    iconId: Int, isError: Boolean, errorMessage: String,
    value: String, isPassword: Boolean = false, onChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(text) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "",
                tint = Color(0xFF607D8B)
            )
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible)
                                android.R.drawable.ic_menu_view
                            else
                                android.R.drawable.ic_secure
                        ),
                        contentDescription = if (passwordVisible) "Сховати пароль" else "Показати пароль",
                        tint = Color(0xFF607D8B)
                    )
                }
            }
        },
        visualTransformation = if (!isPassword || passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2196F3),
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = Color(0xFF2196F3)
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        isError = isError
    )

    // Email Error Message
    if (isError) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 8.dp)
        )
    }
}

@Composable
private fun SwitchButton(text: String, isEnabled: Boolean, onClick: () -> Unit) {
    Column (
        Modifier.padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalDivider(
            color = Color.Gray,
            thickness = 5.dp,
            modifier = Modifier.height(10.dp)
        )
        Button(
            onClick,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.navbar_button),
                disabledContainerColor = Color.LightGray
            ),
            enabled = isEnabled
        ) {
            Text(
                text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun isLoginEnabled(viewModel: LoginViewModel, isLogin: Boolean): Boolean {
    return viewModel.email.isNotEmpty()
            && Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches()
            && viewModel.password.length >= 8
            && if(!isLogin)
        viewModel.username.isNotEmpty() && viewModel.username.length >= 5
    else true
}

// Preview
@Preview(showBackground = true)
@Composable
private fun GreetingsStep2Preview() {
    GreetingsStep2()
}