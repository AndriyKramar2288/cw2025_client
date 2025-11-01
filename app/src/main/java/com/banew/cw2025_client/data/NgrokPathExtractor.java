package com.banew.cw2025_client.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class NgrokPathExtractor {
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
