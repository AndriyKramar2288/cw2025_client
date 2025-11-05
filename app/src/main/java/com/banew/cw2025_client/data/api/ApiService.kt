package com.banew.cw2025_client.data.api

import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.users.UserLoginForm
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_backend_common.dto.users.UserRegisterForm
import com.banew.cw2025_backend_common.dto.users.UserTokenFormResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: UserLoginForm): UserTokenFormResult

    // POST запит - реєстрація
    @POST("auth/register")
    suspend fun register(@Body request: UserRegisterForm): UserTokenFormResult

    @GET("users/")
    suspend fun currentUser(@Header("Authorization") token: String): UserProfileBasicDto

    @GET("course-plan/")
    suspend fun currentCoursePlanList(@Header("Authorization") token: String): List<CoursePlanBasicDto>

    @POST("course-plan/")
    suspend fun createCoursePlan(
        @Header("Authorization") token: String,
        @Body body: CoursePlanBasicDto
    ) : CoursePlanBasicDto
}
