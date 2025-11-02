package com.banew.cw2025_client.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.users.UserLoginForm
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_backend_common.dto.users.UserTokenFormResult
import com.banew.cw2025_client.data.api.ApiService
import com.banew.cw2025_client.ui.greetings.GreetingsActivity
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class DataSource(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    init {
        if (NGROK) {
            NgrokPathExtractor.extractNgrokPath(
                "34tAXbBzXVP23CRpx3aV8lIke4t_3TQ2CQKnWGniuwPzPRmC1"
            ) { path: String? ->
                retrofit = buildClient("$path/api/")
            }
        }
    }

    suspend fun currentCoursePlanList(): Result<List<CoursePlanBasicDto>> {
        return try {
            val list = apiService.currentCoursePlanList("Bearer $token")
            Result.Success(list)
        } catch (e: HttpException) {
            if (e.code() == 403) logout()
            Result.Error(IOException("HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    suspend fun currentUserProfile(): Result<UserProfileBasicDto> {

        if (this.token == null) {
            logout()
        }

        return try {
            val list = apiService.currentUser("Bearer $token")
            Result.Success(list)
        } catch (e: HttpException) {
            if (e.code() == 403) logout()
            Result.Error(IOException("HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    suspend fun login(username: String, password: String): Result<UserTokenFormResult> {

        val form = UserLoginForm(username, password)

        return try {
            val list = apiService.login(form)
            updateToken(list.token)
            Result.Success(list)
        } catch (e: HttpException) {
            if (e.code() == 401) logout()
            Result.Error(IOException("HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    fun logout() {
        // TODO
        // так робити, казали, нізя
        val intent = Intent(context, GreetingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    val token: String?
        get() = prefs.getString("jwt_token", null)

    private fun updateToken(token: String) {
        prefs.edit {
            putString("jwt_token", token)
        }
    }

    companion object {
        private var retrofit: Retrofit? = null
        private const val BASE_URL = "http://10.0.2.2:8080/api/"
        private const val NGROK = false
        private val client: Retrofit?
            get() = retrofit ?: buildClient(BASE_URL)

        private fun buildClient(path: String): Retrofit {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(path)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit!!
        }

        private val apiService: ApiService
            get() = client!!.create<ApiService>(
                ApiService::class.java
            )
    }
}