package com.example.ihm_reparties;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {


// For POST request


    @POST("/start") // specify the sub url for our base url
    Call<StartGame> startGame(@Body StartGame startGame);

    @POST("/addAntimatiere") // specify the sub url for our base url
    Call<AddAntimatiere> addAntimatiere(@Body AddAntimatiere addAntimatiere);


// for GET request

    @GET("/")
    Call<HelloWorldApiResponse> getHelloWorldCall();

    @GET("/action-list")
    Call<List<OrdersApiResponse>> getOrdersApiResponseCall();

    @GET("/game/finish")
    Call<GameFinished> getGameFinishedApiResponseCall();

    @GET("/is-there-no-more-antimatiere")
    Call<NoMoreAntimatiere> getNoMoreAntimatiereApiResponseCall();
// CountryResponse is a POJO class which receives the response of this API

}