package com.example.belikov.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.belikov.myapplication.DBTools.WeatherDataReader;
import com.example.belikov.myapplication.DBTools.WeatherDataSource;
import com.example.belikov.myapplication.DBTools.WeatherNote;
import com.example.belikov.myapplication.interfaces.OpenWeather;
import com.example.belikov.myapplication.model.WeatherDay;
import com.example.belikov.myapplication.model.WeatherForecast;
import com.example.belikov.myapplication.model.WeatherRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Locale locale;

    private SharedPreferences sharedPref;
    private OpenWeather openWeather;

    private TextView temperTextView;
    private TextView humidTextView;
    private TextView windTextView;
    private TextView pressTextView;
    private AutoCompleteTextView city;
    private ImageView imageView;
    private TextView cityName;
    private NavigationView navigationView;
    private View headerLayout;

    public static final String CELSIUS = " °C";
    public static final String FAHRENHEIT = " °F";
    public static final String CITY = "city";
    public static final String CHOOSE_CITY = "choose_city";
    public static final String API_KEY = "edfdd9d40eefbcf9979031dd4a5ff0c5";
    private String currentCity;

    private boolean isCelsius = true;

    private float valueTemper;
    private float valueWind;
    private int valueHumidity;
    private int valuePress;

    private WeatherDataSource notesDataSource;     // Источник данных
    private WeatherDataReader noteDataReader;      // Читатель данных


    private final String[] cities = {"Moscow", "Novosibirsk", "Saint Petersburg,ru", "Nizhniy Novgorod", "Vladivostok"};
    private final String[] citiesImages = {"https://www.rgo.ru/sites/default/files/styles/full_view/public/20.02.2014_ilya_melnikov_moskva_0.jpg?itok=CZ--BHfl",
                                            "https://avatars.mds.yandex.net/get-pdb/812271/9d5c0197-6b9d-499e-b4fd-14f9964e0fe4/s1200",
                                            "https://cdn24.img.ria.ru/images/42278/81/422788117_0:0:600:340_600x0_80_0_0_8c05ee6cd03dcd9c5fa5073111f49514.jpg",
                                            "https://nashaplaneta.net/europe/russia/img_nizhny/kreml-nizhniy_mini.jpg",
                                            "https://img-fotki.yandex.ru/get/467152/30348152.234/0_9311f_52ade03c_orig"};
    private HashMap<String, String> citiesMap= new HashMap<>();
    private String temp; // удалить
    private List<WeatherDay> weatherDayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initDataSource();
        initRetorfit();
        requestRetrofit(currentCity, API_KEY);

    }

    private void initDataSource(){
        notesDataSource = new WeatherDataSource(getApplicationContext());
        notesDataSource.open();
        noteDataReader = notesDataSource.getNoteDataReader();
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


                            temp = weatherDayList.get(0).getTemp();
                            Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onFailure(Call<WeatherForecast> call, Throwable t) {
                        temperTextView.setText(getResources().getString(R.string.temper) + " " + "Error");
                         windTextView.setText(getResources().getString(R.string.wind) + " " + "Error");
                        humidTextView.setText(getResources().getString(R.string.humid) + " " + "Error");
                        pressTextView.setText(getResources().getString(R.string.press) + " " +"Error");
                    }
                });

    }

    private void filteredRespons(Response<WeatherForecast> response) {
        weatherDayList.clear();
        WeatherForecast data = response.body();
//        String a = Integer.toString(data.getItems().get(1).getDate().get(Calendar.HOUR_OF_DAY));
//        Toast.makeText(MainActivity.this, a, Toast.LENGTH_SHORT).show();
        for (WeatherDay day : data.getItems()){

            if (day.getDate().get(Calendar.HOUR_OF_DAY) == 13){
//                Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                weatherDayList.add(day);
            }
        }
    }

    private void addOrUpdate() {
        noteDataReader.open(currentCity);

        if (noteDataReader.getPosition(0) == null) {
//            noteDataReader.close();
//            noteDataReader.open();
            Toast.makeText(MainActivity.this, "Add to DB", Toast.LENGTH_SHORT).show();
            notesDataSource.addNote(currentCity, valueTemper, valueWind, valueHumidity, valuePress);
        } else {
            notesDataSource.editNote(noteDataReader.getPosition(0),currentCity, valueTemper, valueWind, valueHumidity, valuePress);
            Toast.makeText(MainActivity.this, "Edit DB", Toast.LENGTH_SHORT).show();
        }
        noteDataReader.close();
    }

    private void setParams() {
        if (isCelsius)setCelsiusParam();
        else setFahrenParam();
         windTextView.setText(getResources().getString(R.string.wind) + " " + valueWind + " mps");
        humidTextView.setText(getResources().getString(R.string.humid) + " " + valueHumidity + " %");
        pressTextView.setText(getResources().getString(R.string.press) + " " + valuePress + " hpa");
    }

    private void init() {
        currentCity = "Moscow";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        temperTextView = findViewById(R.id.temperature);
        humidTextView = findViewById(R.id.humidity);
        windTextView = findViewById(R.id.wind);
        pressTextView = findViewById(R.id.pressure);

        navigationView = findViewById(R.id.nav_view);
        headerLayout = navigationView.getHeaderView(0);
        imageView = headerLayout.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.pogoda);
        cityName = headerLayout.findViewById(R.id.city_name);
        city = findViewById(R.id.autoCompleteTextView);
        city.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cities));
        city.setText(currentCity);

        sharedPref = getSharedPreferences(CITY, Context.MODE_PRIVATE);

        if (sharedPref.contains(CITY)){
            city.setText(sharedPref.getString(CITY, "city"));
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        Button ok = findViewById(R.id.ok);
        Button forecast = findViewById(R.id.forecast);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCity = city.getText().toString();
//                cityCountry[0] = currentCity;
//                cityCountry[1] = "";
                noteDataReader.open(currentCity);
                WeatherNote note = noteDataReader.getPosition(0);
//                if (note != null) {
//                    Toast.makeText(MainActivity.this, "get from DB", Toast.LENGTH_SHORT).show();
//                    getParamFromDB(note);
//                }else {
//                    requestRetrofit(currentCity, API_KEY);
//                    Toast.makeText(MainActivity.this, "get from internet", Toast.LENGTH_SHORT).show();
//                }
                Toast.makeText(MainActivity.this, "request", Toast.LENGTH_SHORT).show();
                requestRetrofit(currentCity, API_KEY);

                setImageView();

            }
        });

        forecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                intent.putExtra(CHOOSE_CITY, currentCity);
                startActivity(intent);
            }
        });

        mapInit();
        setImageView();
    }

    private void getParamFromDB(WeatherNote note) {
//        Toast.makeText(MainActivity.this, note.getCityName(), Toast.LENGTH_SHORT).show();
            valueTemper = note.getTemper();
            valueWind = note.getWind();
            valueHumidity = note.getHumidity();
            valuePress = note.getPress();
            setParams();
    }

    private void mapInit(){
        for (int i = 0; i < cities.length; i++){
            citiesMap.put(cities[i], citiesImages[i]);
        }
    }

    private void setImageView() {
        currentCity = city.getText().toString();
        cityName.setText(currentCity);
        String path = citiesMap.get(currentCity);
        Picasso
                .with(this)
                .load(path)
                .into(imageView);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.lang) {
            Toast.makeText(this, "lang", Toast.LENGTH_SHORT).show();
            if (Locale.getDefault().getLanguage().equals("en")){
                setLocale("ru");
            } else setLocale("en");
            startActivityForResult(new Intent(this, MainActivity.class), 0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.celsius) {
            isCelsius = true;
            setCelsiusParam();
            Toast.makeText(this, "celsius", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.fahrenheit) {
            isCelsius = false;
            setFahrenParam();
            Toast.makeText(this, "fahrenheit", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.about_dev) {
            Toast.makeText(this, "about developers", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.feedback) {
            Toast.makeText(this, "feedback", Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CITY, city.getText().toString());
        editor.commit();
        super.onDestroy();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.for_ten_days){
            Toast.makeText(this, "create a new fragment", Toast.LENGTH_SHORT).show();
            // create a new fragment

        }
        return true;
    }

    private void setLocale(String lang){
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }

    private void setCelsiusParam(){
        temperTextView.setText(getResources().getString(R.string.temper) + " " + valueTemper + CELSIUS);
    }

    private void setFahrenParam(){
        temperTextView.setText(getResources().getString(R.string.temper) + " " + ((float)((int)(((valueTemper*9/5+32))*10))/10) + FAHRENHEIT);
    }



}


