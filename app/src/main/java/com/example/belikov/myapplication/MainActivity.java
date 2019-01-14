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
import com.example.belikov.myapplication.interfaces.OpenWeather;
import com.example.belikov.myapplication.model.WeatherRequest;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
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

    private static final String CELSIUS = " °C";
    private static final String FAHRENHEIT = " °F";
    public static final String CITY = "city";
    private static final String API_KEY = "edfdd9d40eefbcf9979031dd4a5ff0c5";

    private boolean isCelsius = true;

    private float valueTemper;
    private float valueWind;
    private int valueHumidity;
    private int valuePress;

    private final String[] cities = {"Moscow", "Novosibirsk", "Saint Petersburg,ru", "Nizhniy Novgorod", "Vladivostok"};
    private final String[] citiesImages = {"https://www.rgo.ru/sites/default/files/styles/full_view/public/20.02.2014_ilya_melnikov_moskva_0.jpg?itok=CZ--BHfl",
                                            "https://avatars.mds.yandex.net/get-pdb/812271/9d5c0197-6b9d-499e-b4fd-14f9964e0fe4/s1200",
                                            "https://cdn24.img.ria.ru/images/42278/81/422788117_0:0:600:340_600x0_80_0_0_8c05ee6cd03dcd9c5fa5073111f49514.jpg",
                                            "https://nashaplaneta.net/europe/russia/img_nizhny/kreml-nizhniy_mini.jpg",
                                            "https://img-fotki.yandex.ru/get/467152/30348152.234/0_9311f_52ade03c_orig"};
    private HashMap<String, String> citiesMap= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initRetorfit();
        requestRetrofit(city.getText().toString(), API_KEY);
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
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null)
                            valueTemper = (float)((int)((response.body().getMain().getTemp() - 273)*10))/10;
                            if (isCelsius)setCelsiusParam();
                            else setFahrenParam();
                        valueWind = response.body().getWind().getSpeed();
                        valueHumidity = response.body().getMain().getHumidity();
                        valuePress = response.body().getMain().getPressure();
                        setParams();
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        temperTextView.setText("Error");
                        windTextView.setText("Error");
                        humidTextView.setText("Error");
                        pressTextView.setText("Error");
                    }
                });

    }

    private void setParams() {
        windTextView.setText(getResources().getString(R.string.wind) + " " + valueWind + " mps");
        humidTextView.setText(getResources().getString(R.string.humid) + " " + valueHumidity + " %");
        pressTextView.setText(getResources().getString(R.string.press) + " " + valuePress + " hpa");
    }

    private void init() {

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
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRetrofit(city.getText().toString(), API_KEY);
                setImageView();
            }
        });

        for (int i = 0; i < cities.length; i++){
            citiesMap.put(cities[i], citiesImages[i]);
        }

        setImageView();

    }

    private void setImageView() {
        String currentCity = city.getText().toString();
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


