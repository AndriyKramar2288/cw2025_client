package com.banew.cw2025_client.ui.start

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banew.cw2025_backend_common.dto.users.UserTokenFormResult
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.Result
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource

    var email by mutableStateOf("")

    var password by mutableStateOf("")

    var username by mutableStateOf("")

    var photoSrc by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set

    var loginResult by mutableStateOf<Result<UserTokenFormResult>?>(null)
        private set

    fun login() {
        viewModelScope.launch {
            isLoading = true
            loginResult = dataSource?.login(email, password)
            isLoading = false
        }
    }

    fun register() {
        viewModelScope.launch {
            isLoading = true
            loginResult = dataSource?.register(email, username, photoSrc, password)
            isLoading = false
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String?): Boolean {
        if (username == null) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String?): Boolean {
        return password != null && password.trim { it <= ' ' }.length > 8
    }
}