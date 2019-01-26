package com.example.belikov.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.belikov.myapplication.interfaces.OpenWeather;
import com.example.belikov.myapplication.model.WeatherForecast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);


    }

//    private void initRetorfit(){
//        Retrofit retrofit;
//        retrofit = new Retrofit.Builder()
//// Базовая часть адреса
//                .baseUrl("http://api.openweathermap.org/")
//// Конвертер, необходимый для преобразования JSON в объекты
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//// Создаем объект, при помощи которого будем выполнять запросы
//        openWeather = retrofit.create(OpenWeather.class);
//    }
//
//    private void requestRetrofit(String city, String keyApi){
//        openWeather.loadWeather(city, keyApi)
//                .enqueue(new Callback<WeatherForecast>() {
//                    @Override
//                    public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
//
//
//                        if (response.body() != null) {
////                            valueTemper = (float) ((int) ((response.body().getMain().getTemp() - 273) * 10)) / 10;
////                            valueWind = response.body().getWind().getSpeed();
////                            valueHumidity = response.body().getMain().getHumidity();
////                            valuePress = response.body().getMain().getPressure();
////                            setParams();
////                            addOrUpdate();
//                            filteredRespons(response);
//
//
//                            temp = weatherDayList.get(0).getTemp();
//                            Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
//
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<WeatherForecast> call, Throwable t) {
//                        temperTextView.setText(getResources().getString(R.string.temper) + " " + "Error");
//                        windTextView.setText(getResources().getString(R.string.wind) + " " + "Error");
//                        humidTextView.setText(getResources().getString(R.string.humid) + " " + "Error");
//                        pressTextView.setText(getResources().getString(R.string.press) + " " +"Error");
//                    }
//                });
//
//    }
}
