package com.diordna.whats.near.me.restservice;

import com.diordna.whats.near.me.model.WeatherResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Nahid 002345 on 1/9/2017.
 */

public interface  ApiInterface    {
    @GET("weather")
    Call<WeatherResponseModel> getWeatherByLongLat(@Query("lon") String longitude, @Query("lat") String latitude, @Query("appid") String apiKey, @Query("units") String units);

    @GET("weather")
    Call<WeatherResponseModel> getWeatherByCityName(@Query("q") String cityName, @Query("appid") String apiKey);

}
