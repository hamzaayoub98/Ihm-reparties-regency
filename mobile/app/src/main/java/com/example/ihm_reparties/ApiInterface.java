package com.example.ihm_reparties;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {


// For POST request


    @POST("/finish") // specify the sub url for our base url
    Call<FinishGame> finishGame(@Body FinishGame finishGame);


// for GET request

    @GET("/")
    Call<HelloWorldApiResponse> getHelloWorldCall();

    @GET("/action-list")
    Call<List<OrdersApiResponse>> getOrdersApiResponseCall();

// CountryResponse is a POJO class which receives the response of this API

}