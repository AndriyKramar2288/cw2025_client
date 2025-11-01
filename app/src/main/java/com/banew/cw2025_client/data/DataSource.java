package com.banew.cw2025_client.data;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto;
import com.banew.cw2025_backend_common.dto.users.UserLoginForm;
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto;
import com.banew.cw2025_backend_common.dto.users.UserTokenFormResult;
import com.banew.cw2025_client.ui.greetings.GreetingsActivity;
import com.banew.cw2025_client.R;
import com.banew.cw2025_client.data.api.ApiService;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class DataSource {
    private final SharedPreferences prefs;
    //private final AppDatabase db;

    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private static final boolean isNgrok = false;
    private final Context context;

    private static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = buildClient(BASE_URL);
        }
        return retrofit;
    }

    private static Retrofit buildClient(String path) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(path)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    private static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    public DataSource(Context applicationContext) {
        context = applicationContext;

        prefs = applicationContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        if (isNgrok) {
            NgrokTokenExtractor.extractNgrokPath("", path -> {
                retrofit = buildClient(path + "/api/");
            });
        }
    }

    public CompletableFuture<Result<List<CoursePlanBasicDto>>> getCurrentCoursePlanList() {
        CompletableFuture<Result<List<CoursePlanBasicDto>>> result = new CompletableFuture<>();

        enqueue(
                getApiService().currentCoursePlanList("Bearer " + getToken()),
        list -> {
                    result.complete(new Result.Success<>(list));
                },
                (t) -> {
                    result.complete(new Result.Error<>(new IOException(
                            context.getString(R.string.network_error), t)));
                },
                Map.of(403, this::logout)
        );

        return result;
    }

    public CompletableFuture<Result<UserProfileBasicDto>> getCurrentUserProfile() {
        CompletableFuture<Result<UserProfileBasicDto>> result = new CompletableFuture<>();

        if (getToken() == null) {
            logout();
        }

        enqueue(
                getApiService().currentUser("Bearer " + getToken()),
                user -> {
                    result.complete(new Result.Success<>(user));
                },
                (t) -> {
                    result.complete(new Result.Error<>(new IOException(
                            context.getString(R.string.network_error), t)));
                },
                Map.of(403, this::logout)
        );

        return result;
    }

    public CompletableFuture<Result<UserTokenFormResult>> login(String username, String password) {
        CompletableFuture<Result<UserTokenFormResult>> result = new CompletableFuture<>();

        var form = new UserLoginForm(username, password);
        enqueue(
                getApiService().login(form),
                resBody -> {
                    result.complete(new Result.Success<>(resBody));
                    updateToken(resBody.token());
                },
                (t) -> {
                    result.complete(new Result.Error<>(new IOException(
                            context.getString(R.string.network_error), t)));
                },
                Map.of(400, () -> {
                    result.complete(new Result.Error<>(new IOException(
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

    private static class NgrokTokenExtractor {
        public static void extractNgrokPath(String token, Consumer<String> consumer) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.ngrok.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            NgrokApiService service = retrofit.create(NgrokApiService.class);

            try {
                service
                .getTunnels("Bearer " + token, "2")
                .enqueue(new Callback<NgrokTunnelsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NgrokTunnelsResponse> call,
                                           @NonNull Response<NgrokTunnelsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String path = response.body().tunnels.stream()
                                    .findFirst()
                                    .orElseThrow()
                                    .publicUrl;

                            System.out.println("Я ЖИВИЙЙЙ\n\n\n\n" + path);
                            consumer.accept(path);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<NgrokTunnelsResponse> call,
                                          @NonNull Throwable t) {

                    }
                });
            }
            catch (Exception e) {
                throw new RuntimeException("Oh shit!", e);
            }
        }

        private interface NgrokApiService {
            @GET("tunnels")
            Call<NgrokTunnelsResponse> getTunnels(
                    @Header("Authorization") String bearerToken,
                    @Header("Ngrok-Version") String version
            );
        }

        private static class NgrokTunnelsResponse {
            public List<NgrokTunnel> tunnels;
        }

        private static class NgrokTunnel {
            @SerializedName("public_url")
            public String publicUrl;
        }
    }
}