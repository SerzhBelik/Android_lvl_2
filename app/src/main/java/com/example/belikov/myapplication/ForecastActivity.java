package com.example.belikov.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.belikov.myapplication.interfaces.OpenWeather;
import com.example.belikov.myapplication.model.WeatherDay;
import com.example.belikov.myapplication.model.WeatherForecast;
import com.example.belikov.myapplication.tools.DataSourceBuilder;
import com.example.belikov.myapplication.tools.WeatherAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastActivity extends Activity {
    private OpenWeather openWeather;
    private String temp;
    private List<WeatherDay> weatherDayList = new ArrayList<>();
    private String[] dateData = new String[5];
    private String[] tempData = new String[5];
    private String[] windData = new String[5];
    private String[] humidData= new String[5];
    private String[] pressData= new String[5];
    private WeatherForecast data;
    private DataSourceBuilder builder;
    private WeatherAdapter adapter;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);

        init();

        initRetorfit();
        requestRetrofit(getIntent().getStringExtra(MainActivity.CHOOSE_CITY), MainActivity.API_KEY);







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
//                            valueTemper = (float) ((int) ((response.body().getMain().getTemp() - 273) * 10)) / 10;
//                            valueWind = response.body().getWind().getSpeed();
//                            valueHumidity = response.body().getMain().getHumidity();
//                            valuePress = response.body().getMain().getPressure();
//                            setParams();
//                            addOrUpdate();
                            filteredRespons(response);


//                            temp = weatherDayList.get(0).getTemp();
//                            Toast.makeText(ForecastActivity.this, temp, Toast.LENGTH_SHORT).show();
                            formWeatherData();

                            setWeatherCards();

                        }


                    }

                    @Override
                    public void onFailure(Call<WeatherForecast> call, Throwable t) {
//                        temperTextView.setText(getResources().getString(R.string.temper) + " " + "Error");
//                        windTextView.setText(getResources().getString(R.string.wind) + " " + "Error");
//                        humidTextView.setText(getResources().getString(R.string.humid) + " " + "Error");
//                        pressTextView.setText(getResources().getString(R.string.press) + " " +"Error");
                    }
                });

    }



    private void filteredRespons(Response<WeatherForecast> response) {
        weatherDayList.clear();
        data = response.body();
//        String a = Integer.toString(data.getItems().get(1).getDate().get(Calendar.HOUR_OF_DAY));
//        Toast.makeText(MainActivity.this, a, Toast.LENGTH_SHORT).show();
        for (WeatherDay day : data.getItems()){

            if (day.getDate().get(Calendar.HOUR_OF_DAY) == 13){
//                Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                weatherDayList.add(day);
            }
        }
    }

    private void init() {

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < dateData.length; i++){
//
//        }


        String td = getResources().getString(R.string.temper);
        String wd = getResources().getString(R.string.wind);
        String hd = getResources().getString(R.string.humid);
        String pd = getResources().getString(R.string.press);
        for (int i = 0; i < tempData.length; i++) {
            dateData[i] = "date";
            tempData [i]= td;
            windData [i]= wd;
            humidData[i]= hd;
            pressData[i]= pd;
        }

//        temp = tempData[0];
//        Toast.makeText(ForecastActivity.this, temp, Toast.LENGTH_SHORT).show();

        builder = new DataSourceBuilder(dateData, tempData, windData, humidData, pressData);

        adapter = new WeatherAdapter(builder.build());
        rv.setAdapter(adapter);

    }

    private void formWeatherData() {
        for (int i = 0; i < weatherDayList.size(); i++) {
            tempData[i] = getResources().getString(R.string.temper) + " " +
                    String.valueOf(Integer.parseInt(weatherDayList.get(i).getTemp().substring(0, 3)) - 273) +
                    MainActivity.CELSIUS;

            windData[i] = getResources().getString(R.string.wind) + " " +
                    " " + weatherDayList.get(i).getWind().getSpeed() + getResources().getString(R.string.mps);

            humidData[i] = getResources().getString(R.string.humid) + " " +
                    weatherDayList.get(i).getHumid() + "%";

            pressData[i] = getResources().getString(R.string.press) + " " +
                    weatherDayList.get(i).getPress() + " " + getResources().getString(R.string.hpa);
            }
        Toast.makeText(ForecastActivity.this, pressData[0], Toast.LENGTH_SHORT).show();

//        temp = tempData[0];
//        Toast.makeText(ForecastActivity.this, temp, Toast.LENGTH_SHORT).show();

        }

    private void setWeatherCards() {
        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        builder = new DataSourceBuilder(dateData, tempData, windData, humidData, pressData);

        adapter = new WeatherAdapter(builder.build());
        rv.setAdapter(adapter);
    }


}
