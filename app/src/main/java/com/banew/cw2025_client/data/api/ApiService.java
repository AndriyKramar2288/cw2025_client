package com.banew.cw2025_client.data.api;

import com.banew.cw2025_backend_common.dto.users.UserLoginForm;
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto;
import com.banew.cw2025_backend_common.dto.users.UserRegisterForm;
import com.banew.cw2025_backend_common.dto.users.UserTokenFormResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<UserTokenFormResult> login(@Body UserLoginForm request);

    // POST запит - реєстрація
    @POST("auth/register")
    Call<UserTokenFormResult> register(@Body UserRegisterForm request);

    @GET("users/")
    Call<UserProfileBasicDto> currentUser(@Header("Authorization") String token);
}
