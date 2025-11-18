package com.banew.cw2025_client.data.api

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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body request: UserLoginForm): UserTokenFormResult

    @POST("auth/register")
    suspend fun register(@Body request: UserRegisterForm): UserTokenFormResult


    // --- USER ---
    @GET("users/")
    suspend fun currentUser(
        @Header("Authorization") token: String
    ): UserProfileDetailedDto

    @GET("users/{userId}")
    suspend fun userProfileById(
        @Path("userId") userId: Long,
        @Header("Authorization") token: String
    ): UserProfileDetailedDto


    // --- COURSE PLAN ---
    @GET("course-plan/{courseId}")
    suspend fun loadCoursePlanById(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Long
    ): CoursePlanBasicDto

    @GET("course-plan/")
    suspend fun currentCoursePlanList(
        @Header("Authorization") token: String
    ): List<CoursePlanBasicDto>

    @POST("course-plan/")
    suspend fun createCoursePlan(
        @Header("Authorization") token: String,
        @Body body: CoursePlanBasicDto
    ): CoursePlanBasicDto


    // --- COURSE ---
    @GET("course/")
    suspend fun getUserCourses(
        @Header("Authorization") token: String
    ): List<CourseBasicDto>

    @GET("course/by-plan/{courseId}")
    suspend fun getCourse(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Long
    ): CourseDetailedDto

    @POST("course/by-plan/{courseId}/start")
    suspend fun beginCourse(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Long
    ): CourseBasicDto

    @POST("course/by-plan/{courseId}/topic/{topicId}/start")
    suspend fun beginTopic(
        @Header("Authorization") token: String,
        @Path("topicId") topicId: Long,
        @Path("courseId") courseId: Long
    ): TopicCompendiumDto

    @PUT("course/by-plan/{courseId}/topic")
    suspend fun updateCompendium(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Long,
        @Body body: TopicCompendiumDto
    ): TopicCompendiumDto

    // --- FLASH CARDS ---

    @GET("cards")
    suspend fun getCurrentCards(
        @Header("Authorization") token: String
    ): List<FlashCardBasicDto>

    @GET("cards/stats")
    suspend fun getCardDailyStats(
        @Header("Authorization") token: String
    ): FlashCardDayStats

    @PUT("cards/{flashCardId}/answer")
    suspend fun answerFlashCard(
        @Header("Authorization") token: String,
        @Path("flashCardId") flashCardId: Long,
        @Body body: FlashCardAnswer
    ): FlashCardBasicDto

    @PUT("cards/{flashCardId}/concept")
    suspend fun updateConcept(
        @Header("Authorization") token: String,
        @Path("flashCardId") flashCardId: Long,
        @Body body: TopicCompendiumDto.ConceptBasicDto
    ): FlashCardBasicDto
}
