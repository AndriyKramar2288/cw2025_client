package com.banew.cw2025_client.ui.start

import android.util.Patterns
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.banew.cw2025_client.ui.components.ErrorBox
import com.banew.cw2025_client.ui.components.LoadingBox
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
                text = stringResource(R.string.login_form_hello),
                style = AppTypography.headlineLarge,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colorResource(R.color.navbar_button).copy(alpha = 0.7f)
                    )
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.login_form_label),
                style = AppTypography.bodyLarge,
                color = Color.White
            )
        }
        Row(
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            SwitchButton(
                text = stringResource(R.string.login_form_login),
                isEnabled = !isLogin,
            ) {
                isLogin = true
            }
            SwitchButton(
                text = stringResource(R.string.login_form_sign_in),
                isEnabled = isLogin,
            ) {
                isLogin = false
            }
        }

        // Email Field
        FormField(
            stringResource(R.string.login_form_field_email),
            "example@email.com",
            android.R.drawable.ic_dialog_email,
            viewModel.email,
            viewModel.email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(
                viewModel.email
            ).matches(),
            stringResource(R.string.login_form_field_email_alert),
        ) { viewModel.email = it }

        if (!isLogin) {
            // Username Field
            FormField(
                stringResource(R.string.login_form_field_username),
                stringResource(R.string.login_form_field_username_placeholder),
                android.R.drawable.star_on,
                viewModel.username,
                viewModel.username.isNotEmpty() && viewModel.username.length <= 5,
                stringResource(R.string.login_form_field_username_alert),
            ) { viewModel.username = it }
            // Photo Field
            FormField(
                stringResource(R.string.login_form_field_photo),
                stringResource(R.string.login_form_field_photo_placeholder),
                android.R.drawable.ic_menu_mapmode,
                viewModel.photoSrc
            ) { viewModel.photoSrc = it }

            AsyncImage( // coil-compose
                model = viewModel.photoSrc,
                contentDescription = "Photo",
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
            stringResource(R.string.login_form_field_password),
            stringResource(R.string.login_form_field_password_placeholder),
            android.R.drawable.ic_lock_idle_lock,
            viewModel.password,
            viewModel.password.isNotEmpty() && viewModel.password.length < 8,
            stringResource(R.string.login_form_field_password_alert),
            isPassword = true,
        ) { viewModel.password = it }

        Spacer(modifier = Modifier.height(8.dp))

        // Login Error Message
        viewModel.loginResult?.let { result ->
            if (result.isError) {
                ErrorBox(
                    text = stringResource(R.string.login_form_alert),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
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
                containerColor = colorResource(R.color.navbar_button2),
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
                    text =
                        if (isLogin)
                            stringResource(R.string.login_form_login)
                        else
                            stringResource(R.string.login_form_sign_in),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    // Loading Overlay
    if (viewModel.isLoading) {
        LoadingBox(stringResource(R.string.login_form_loading))
    }
}

@Composable
private fun FormField(
    text: String, placeholder: String,
    iconId: Int, value: String,
    isError: Boolean = false, errorMessage: String = "",
    isPassword: Boolean = false, onChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        textStyle = AppTypography.bodyMedium,
        label = { Text(text, style = AppTypography.bodyMedium) },
        placeholder = { Text(placeholder, style = AppTypography.bodySmall) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "Login icon",
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
                        contentDescription = if (passwordVisible) "hide" else "show",
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
            focusedBorderColor = Color(0xFF4E3715),
            unfocusedBorderColor = Color.LightGray,
            unfocusedTextColor = Color.DarkGray,
            focusedTextColor = Color.Black,
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
            style = AppTypography.bodySmall,
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