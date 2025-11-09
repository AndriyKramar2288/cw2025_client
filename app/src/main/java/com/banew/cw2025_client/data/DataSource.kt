package com.banew.cw2025_client.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import com.banew.cw2025_backend_common.dto.BasicResult
import com.banew.cw2025_backend_common.dto.FieldExceptionResult
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.courses.CourseBasicDto
import com.banew.cw2025_backend_common.dto.courses.TopicCompendiumDto
import com.banew.cw2025_backend_common.dto.users.UserLoginForm
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_backend_common.dto.users.UserRegisterForm
import com.banew.cw2025_backend_common.dto.users.UserTokenFormResult
import com.banew.cw2025_client.data.api.ApiService
import com.banew.cw2025_client.ui.start.StartActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.time.Instant
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

    suspend fun beginCourse(coursePlanId: Long): Result<CourseBasicDto> {
        if (token == null) {
            logout()
            return Result.Error(IOException("Not authorized!"))
        }

        return try {
            val result = apiService.beginCourse("Bearer $token", coursePlanId)
            Result.Success(result)
        } catch (e: HttpException) {
            Result.Error(IOException(resolveHttpException(e)))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    suspend fun createCoursePlan(
        name: String,
        desc: String,
        topics: List<TopicForm>
    ): Result<CoursePlanBasicDto> {

        if (token == null) {
            logout()
            return Result.Error(IOException("Not authorized!"))
        }

        return try {
            val result = apiService.createCoursePlan("Bearer $token", CoursePlanBasicDto(
                null,
                name,
                null,
                desc,
                topics.map { CoursePlanBasicDto.TopicBasicDto(
                    null, it.name.value, it.desc.value
                ) }
            ))
            Result.Success(result)
        } catch (e: HttpException) {
            Result.Error(IOException(resolveHttpException(e)))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    private fun resolveHttpException(e: HttpException) : String =
        when (e.code()) {
            401 -> {
                if (e.code() == 401) logout()
                "HTTP ${e.code()}"
            }
            400 -> {
                val stringObj = e.response()?.errorBody()?.string()
                val message =
                    if (stringObj != null) {
                        try {
                            val obj = Gson()
                                .fromJson(stringObj, FieldExceptionResult::class.java)

                            "${obj.message()}\n${obj.fieldErrors.joinToString(separator = "\n")
                            { "${it.field} - ${it.message}" }}"
                        }
                        catch (ex: NullPointerException) {
                            Gson()
                                .fromJson(stringObj, BasicResult::class.java)
                                .message()
                        }
                    }
                    else "Введені дані некоректні!"

                message
            }
            else -> "HTTP ${e.code()}"
        }

    suspend fun currentCourseList(): Result<List<CourseBasicDto>> {
        return try {
            val list = apiService.getUserCourses("Bearer $token")
            Result.Success(list)
        } catch (e: HttpException) {
            Result.Error(IOException(resolveHttpException(e)))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    suspend fun currentCoursePlanList(): Result<List<CoursePlanBasicDto>> {
        return try {
            val list = apiService.currentCoursePlanList("Bearer $token")
            Result.Success(list)
        } catch (e: HttpException) {
            Result.Error(IOException(resolveHttpException(e)))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    suspend fun currentUserProfile(): Result<UserProfileBasicDto> {

        if (token == null) {
            logout()
            return Result.Error(IOException("Not authorized!"))
        }

        return try {
            val list = apiService.currentUser("Bearer $token")
            Result.Success(list)
        } catch (e: HttpException) {
            if (e.code() == 401) logout()
            Result.Error(IOException("HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    suspend fun login(email: String, password: String): Result<UserTokenFormResult> {

        val form = UserLoginForm(email, password)

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

    suspend fun register(
        email: String,
        username: String,
        photoSrc: String,
        password: String
    ): Result<UserTokenFormResult>? {

        val form = UserRegisterForm(
            email, username, photoSrc.ifBlank { "" }, password
        )

        return try {
            val list = apiService.register(form)
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
        val intent = Intent(context, StartActivity::class.java)
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

    suspend fun beginTopic(topicId: Long) : Result<TopicCompendiumDto> {
        return try {
            val list = apiService.beginTopic("Bearer $token", topicId)
            Result.Success(list)
        } catch (e: HttpException) {
            if (e.code() == 401) logout()
            Result.Error(IOException("HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    suspend fun updateCompendium(compendium: TopicCompendiumDto) : Result<TopicCompendiumDto> {
        return try {
            val list = apiService.updateCompendium("Bearer $token", compendium)
            Result.Success(list)
        } catch (e: HttpException) {
            if (e.code() == 401) logout()
            Result.Error(IOException("HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    data class TopicForm(
        var name : MutableState<String> = mutableStateOf(""),
        var desc : MutableState<String> = mutableStateOf("")
    ) {
        constructor (name : String, desc : String) : this(
            mutableStateOf(name),
            mutableStateOf(desc)
        )
    }
    companion object {
        private var retrofit: Retrofit? = null
        private const val BASE_URL = "http://10.0.2.2:8080/api/"
        private const val NGROK = false
        private val client: Retrofit?
            get() = retrofit ?: buildClient(BASE_URL)

        private fun buildClient(path: String): Retrofit {

            val gson = GsonBuilder()
                .registerTypeAdapter(Instant::class.java, object : JsonDeserializer<Instant> {
                    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Instant {
                        return Instant.parse(json!!.asString)
                    }
                })
                .registerTypeAdapter(Instant::class.java, object : JsonSerializer<Instant> {
                    override fun serialize(src: Instant?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                        return JsonPrimitive(src?.toString())
                    }
                })
                .create()

            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(path)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit!!
        }

        private val apiService: ApiService
            get() = client!!.create<ApiService>(
                ApiService::class.java
            )
    }
}