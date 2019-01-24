package com.example.belikov.myapplication.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import com.example.belikov.myapplication.model.WeatherForecast;
import com.example.belikov.myapplication.model.WeatherRequest;

public interface OpenWeather {
    @GET("data/2.5/forecast")
    Call<WeatherForecast> loadWeather(@Query("q") String[] cityCountry, @Query("appid") String keyApi);
}

