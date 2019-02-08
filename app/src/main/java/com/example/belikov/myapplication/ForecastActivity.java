package com.example.belikov.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.belikov.myapplication.interfaces.OpenWeather;
import com.example.belikov.myapplication.model.WeatherDay;
import com.example.belikov.myapplication.model.WeatherForecast;
import com.example.belikov.myapplication.tools.DataSourceBuilder;
import com.example.belikov.myapplication.tools.WeatherAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastActivity extends AppCompatActivity{
    private OpenWeather openWeather;
    private String currentCity;
    private List<WeatherDay> weatherDayList = new ArrayList<>();
    private String[] dateData = new String[5];
    private String[] tempData = new String[5];
    private String[] windData = new String[5];
    private String[] humidData= new String[5];
    private String[] pressData= new String[5];
    private WeatherForecast data;
    private DataSourceBuilder builder;
    private WeatherAdapter adapter;
    private Calendar calendar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);
        currentCity = getIntent().getStringExtra(MainActivity.CHOOSE_CITY);
        init();
        initRetorfit();
        requestRetrofit(currentCity, MainActivity.API_KEY);
    }



    private void initRetorfit(){
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
// Базовая часть адреса
                .baseUrl("http://api.openweathermap.org/")
// Конвертер, необходимый для преобразования JSON в объекты
                .addConverterFactory(GsonConverterFactory.create())
                .build();
// Создаем объект, при помощи которого будем выполнять запросы
        openWeather = retrofit.create(OpenWeather.class);
    }

    private void requestRetrofit(String city, String keyApi){
        openWeather.loadWeather(city, keyApi)
                .enqueue(new Callback<WeatherForecast>() {
                    @Override
                    public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {


                        if (response.body() != null) {
                            filteredRespons(response);
                            formWeatherData();
                            setWeatherCards();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherForecast> call, Throwable t) {

                    }
                });

    }



    private void filteredRespons(Response<WeatherForecast> response) {
        weatherDayList.clear();
        data = response.body();
        for (WeatherDay day : data.getItems()){

            if (day.getDate().get(Calendar.HOUR_OF_DAY) == 13){
                weatherDayList.add(day);
            }
        }
    }

    private void init() {

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        calendar = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String date;
        String td = getResources().getString(R.string.temper);
        String wd = getResources().getString(R.string.wind);
        String hd = getResources().getString(R.string.humid);
        String pd = getResources().getString(R.string.press);
        for (int i = 0; i < tempData.length; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = format1.format(calendar.getTime());
            dateData[i] = date;
            tempData [i]= td;
            windData [i]= wd;
            humidData[i]= hd;
            pressData[i]= pd;
        }

        builder = new DataSourceBuilder(dateData, tempData, windData, humidData, pressData);
        adapter = new WeatherAdapter(builder.build());
        rv.setAdapter(adapter);


    }

    private void formWeatherData() {
        for (int i = 0; i < weatherDayList.size(); i++) {
            if (MainActivity.isCelsius()) {
                tempData[i] = getResources().getString(R.string.temper) + " " +
                        String.valueOf((int) (((weatherDayList.get(i).getTemp() - 273) * 10)) / 10) +
                        MainActivity.CELSIUS;
            } else tempData[i] = getResources().getString(R.string.temper) + " " +
                    String.valueOf(((float)((int)(((weatherDayList.get(i).getTemp()-273)*9/5+32))*10))/10) +
                    MainActivity.FAHRENHEIT;


            windData[i] = getResources().getString(R.string.wind) + " " +
                    " " + weatherDayList.get(i).getWind().getSpeed() + " " + getResources().getString(R.string.mps);

            humidData[i] = getResources().getString(R.string.humid) + " " +
                    weatherDayList.get(i).getHumid() + "%";

            pressData[i] = getResources().getString(R.string.press) + " " +
                    weatherDayList.get(i).getPress() + " " + getResources().getString(R.string.hpa);
            }

        }

    private void setWeatherCards() {
        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        builder = new DataSourceBuilder(dateData, tempData, windData, humidData, pressData);
        TextView headCity = findViewById(R.id.head_city);
        headCity.setText(currentCity);

        adapter = new WeatherAdapter(builder.build());
        rv.setAdapter(adapter);

    }


}
