package com.example.ihm_reparties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserServiceClient
{
    public static void main(String[] args)
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        ApiInterface service = retrofit.create(ApiInterface.class);

        // Calling '/api/users/2'
        Call<HelloWorldApiResponse> callSync = service.getHelloWorldCall();

        try {
            Response<HelloWorldApiResponse> response = callSync.execute();
            HelloWorldApiResponse apiResponse = response.body();
            System.out.println(apiResponse.getHelloworld());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}