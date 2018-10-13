package com.galen.opennotes.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Galen on 4/27/17.
 */

public interface OpenWeatherInterface {


    @GET("data/2.5/weather?APPID=2a6d7afdb0d6c73a85728ef6faac2c67")
    Call<Example> getWeather(@Query("lat") String lat,
                             @Query("lon") String lon);


}
