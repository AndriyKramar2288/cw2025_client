package com.banew.cw2025_client.data;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.banew.cw2025_backend_common.dto.BasicResult;
import com.banew.cw2025_backend_common.dto.UserLoginForm;
import com.banew.cw2025_backend_common.dto.UserProfileBasicDto;
import com.banew.cw2025_backend_common.dto.UserTokenFormResult;
import com.banew.cw2025_client.GreetingsActivity;
import com.banew.cw2025_client.R;
import com.banew.cw2025_client.data.api.ApiService;
import com.banew.cw2025_client.data.model.AppDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataSource {
    private final SharedPreferences prefs;
    //private final AppDatabase db;

    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private final Context context;

    private static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    public DataSource(Context applicationContext) {
        context = applicationContext;
        
//        db = Room.databaseBuilder(
//                applicationContext,
//                AppDatabase.class,
//                "my-database"
//        ).build();

        prefs = applicationContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
    }

    public LiveData<Result<UserProfileBasicDto>> getCurrentUserProfile() {
        MutableLiveData<Result<UserProfileBasicDto>> result = new MutableLiveData<>();

        if (getToken() == null) throw new RuntimeException("Токена нема!!!");

        enqueue(
                getApiService().currentUser("Bearer " + getToken()),
                user -> {
                    result.postValue(new Result.Success<>(user));
                },
                (t) -> {
                    result.postValue(new Result.Error<>(new IOException(
                            context.getString(R.string.network_error), t)));
                },
                Map.of(403, this::logout)
        );

        return result;
    }

    public LiveData<Result<UserTokenFormResult>> login(String username, String password) {
        MutableLiveData<Result<UserTokenFormResult>> result = new MutableLiveData<>();

        var form = new UserLoginForm();
        form.setEmail(username);
        form.setPassword(password);
        enqueue(
                getApiService().login(form),
                resBody -> {
                    result.postValue(new Result.Success<>(resBody));
                    updateToken(resBody.getToken());
                },
                (t) -> {
                    result.postValue(new Result.Error<>(new IOException(
                            context.getString(R.string.network_error), t)));
                },
                Map.of(400, () -> {
                    result.postValue(new Result.Error<>(new IOException(
                            context.getString(R.string.login_error))));
                })
        );

        return result;
    }

    private <T> void enqueue(Call<T> call,
                             Consumer<T> onSuccessBody,
                             Consumer<Throwable> onInternetError,
                             Map<Integer, Runnable> onFailure) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(
                    @NonNull Call<T> call,
                    @NonNull Response<T> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    onSuccessBody.accept(response.body());
                }
                else {
                    Runnable r = onFailure.get(response.code());
                    if (r != null) r.run();
                }
            }
            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                onInternetError.accept(t);
            }
        });
    }

    @Nullable
    public String getToken() {
        return prefs.getString("jwt_token", null);
    }

    private void updateToken(@Nullable String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jwt_token", token);
        editor.apply();
    }

    public void logout() {
        // TODO
        // так робити, казали, нізя
        Intent intent = new Intent(context, GreetingsActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}