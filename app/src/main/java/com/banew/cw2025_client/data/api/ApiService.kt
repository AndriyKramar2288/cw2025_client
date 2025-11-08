package com.banew.cw2025_client.data.api

import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.courses.CourseBasicDto
import com.banew.cw2025_backend_common.dto.courses.TopicCompendiumDto
import com.banew.cw2025_backend_common.dto.users.UserLoginForm
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_backend_common.dto.users.UserRegisterForm
import com.banew.cw2025_backend_common.dto.users.UserTokenFormResult
import retrofit2.Call
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
    ): UserProfileBasicDto


    // --- COURSE PLAN ---
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

    @POST("course/by-plan/{courseId}/start")
    suspend fun beginCourse(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Long
    ): CourseBasicDto

    @POST("course/topic/{topicId}/start")
    suspend fun beginTopic(
        @Header("Authorization") token: String,
        @Path("topicId") topicId: Long
    ): TopicCompendiumDto

    @PUT("course/topic/")
    suspend fun updateCompendium(
        @Header("Authorization") token: String,
        @Body body: TopicCompendiumDto
    ): TopicCompendiumDto
}
