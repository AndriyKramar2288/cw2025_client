package com.banew.cw2025_client.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import com.banew.cw2025_backend_common.dto.BasicResult
import com.banew.cw2025_backend_common.dto.FieldExceptionResult
import com.banew.cw2025_backend_common.dto.cards.FlashCardAnswer
import com.banew.cw2025_backend_common.dto.cards.FlashCardBasicDto
import com.banew.cw2025_backend_common.dto.cards.FlashCardDayStats
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.courses.CourseBasicDto
import com.banew.cw2025_backend_common.dto.courses.CourseDetailedDto
import com.banew.cw2025_backend_common.dto.courses.TopicCompendiumDto
import com.banew.cw2025_backend_common.dto.users.UserLoginForm
import com.banew.cw2025_backend_common.dto.users.UserProfileDetailedDto
import com.banew.cw2025_backend_common.dto.users.UserRegisterForm
import com.banew.cw2025_backend_common.dto.users.UserTokenFormResult
import com.banew.cw2025_client.data.api.ApiService
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
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

class DataSource(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    private suspend fun <T> resolveResult(successCallback: (T) -> Unit = {}, resultSource: (suspend () -> T)) = try {
        val result = resultSource()
        successCallback(result)
        Result.Success(result)
    } catch (e: HttpException) {
        Result.Error(IOException(resolveHttpException(e)))
    } catch (e: Exception) {
        Result.Error(IOException("Network error", e))
    }

    suspend fun beginCourse(coursePlanId: Long): Result<CourseBasicDto> {
        return resolveResult {
            apiService.beginCourse("Bearer $token", coursePlanId)
        }
    }

    suspend fun endCourse(coursePlanId: Long): Result<CourseDetailedDto> {
        return resolveResult {
            apiService.endCourse("Bearer $token", coursePlanId)
        }
    }

    suspend fun createCoursePlan(
        name: String,
        desc: String,
        topics: List<TopicForm>
    ): Result<CoursePlanBasicDto> {
        return resolveResult {
            apiService.createCoursePlan("Bearer $token", CoursePlanBasicDto(
                null,
                name,
                null,
                desc,
                topics.map { CoursePlanBasicDto.TopicBasicDto(
                    null, it.name.value, it.desc.value
                ) }
            ))
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
                        catch (_: NullPointerException) {
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
        return resolveResult {
            apiService.getUserCourses("Bearer $token")
        }
    }

    suspend fun courseDetailedById(coursePlanId: Long): Result<CourseDetailedDto> {
        return resolveResult {
            apiService.getCourse("Bearer $token", coursePlanId)
        }
    }

    suspend fun currentCoursePlanList(query: String): Result<List<CoursePlanBasicDto>> {
        return resolveResult {
            apiService.currentCoursePlanList("Bearer $token", query.ifBlank { null })
        }
    }

    suspend fun userProfileDetailed(userId: Long? = null): Result<UserProfileDetailedDto> {

        if (token == null) {
            logout()
            return Result.Error(IOException("Not authorized!"))
        }

        return resolveResult {
            if (userId == null)
                apiService.currentUser("Bearer $token")
            else
                apiService.userProfileById(userId, "Bearer $token")
        }
    }

    suspend fun login(email: String, password: String): Result<UserTokenFormResult> {

        val form = UserLoginForm(email, password)

        return resolveResult({
            updateToken(it.token)
        }) {
            apiService.login(form)
        }
    }

    suspend fun register(
        email: String,
        username: String,
        photoSrc: String,
        password: String
    ): Result<UserTokenFormResult> {

        val form = UserRegisterForm(
            email, username, photoSrc.ifBlank { "" }, password
        )

        return resolveResult({
            updateToken(it.token)
        }) {
            apiService.register(form)
        }
    }

    fun logout() {
        clearToken()
    }

    val token: String?
        get() = prefs.getString("jwt_token", null)

    private fun clearToken() {
        prefs.edit {
            remove("jwt_token")
        }
    }

    private fun updateToken(token: String) {
        prefs.edit {
            putString("jwt_token", token)
        }
    }

    suspend fun beginTopic(topicId: Long, courseId: Long) : Result<TopicCompendiumDto> {
        return resolveResult {
            apiService.beginTopic("Bearer $token", topicId, courseId)
        }
    }

    suspend fun updateCompendium(compendium: TopicCompendiumDto, courseId: Long) : Result<TopicCompendiumDto> {
        return resolveResult {
            apiService.updateCompendium("Bearer $token", courseId, compendium)
        }
    }

    suspend fun loadCoursePlanById(id: Long): Result<CoursePlanBasicDto> {
        return resolveResult {
            apiService.loadCoursePlanById("Bearer $token", id)
        }
    }

    suspend fun getCardsDailyStats(): Result<FlashCardDayStats> {
        return resolveResult {
            apiService.getCardDailyStats("Bearer $token")
        }
    }

    suspend fun getFlashCardList(): Result<List<FlashCardBasicDto>> {
        return resolveResult {
            apiService.getCurrentCards("Bearer $token")
        }
    }

    suspend fun answerFlashCard(flashCardId: Long, body: FlashCardAnswer)
            : Result<FlashCardBasicDto> {
        return resolveResult {
            apiService.answerFlashCard("Bearer $token", flashCardId, body)
        }
    }

    suspend fun updateConcept(flashCardId: Long, body: TopicCompendiumDto.ConceptBasicDto)
            : Result<FlashCardBasicDto> {
        return resolveResult {
            apiService.updateConcept("Bearer $token", flashCardId, body)
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
        private const val PROD_URL = "https://cw2025-backend.onrender.com/api/"
        private const val PROD = false
        private const val CONNECT_TIMEOUT = 10L
        private const val READ_TIMEOUT = 60L
        private val client: Retrofit
            get() = retrofit ?: buildClient(if (PROD) PROD_URL else BASE_URL)

        private fun buildClient(path: String): Retrofit {

            val gson = GsonBuilder()
                // Instant deserializer
                .registerTypeAdapter(Instant::class.java, object : JsonDeserializer<Instant> {
                    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Instant {
                        return Instant.parse(json!!.asString)
                    }
                })
                // Instant serializer
                .registerTypeAdapter(Instant::class.java, object : JsonSerializer<Instant> {
                    override fun serialize(src: Instant?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                        return JsonPrimitive(src?.toString())
                    }
                })
                // Duration deserializer
                .registerTypeAdapter(Duration::class.java, object : JsonDeserializer<Duration> {
                    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Duration {
                        return Duration.parse(json!!.asString)
                    }
                })
                // Duration serializer
                .registerTypeAdapter(Duration::class.java, object : JsonSerializer<Duration> {
                    override fun serialize(src: Duration?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                        return JsonPrimitive(src?.toString())
                    }
                })
                .create()

            val client = OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(path)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit!!
        }

        private val apiService: ApiService
            get() = client.create<ApiService>(
                ApiService::class.java
            )
    }
}