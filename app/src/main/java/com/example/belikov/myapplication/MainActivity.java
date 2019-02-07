package com.example.belikov.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.example.belikov.myapplication.interfaces.OpenWeather;
import com.example.belikov.myapplication.model.WeatherDay;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
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
    private static final String UNIT = "metric";
    private static final int PERMISSION_REQUEST_CODE = 10;
    private String currentCity;

    private boolean isCelsius = true;

    private float valueTemper;
    private float valueWind;
    private int valueHumidity;
    private int valuePress;
    private double lat;
    private double lon;

    private WeatherDataSource notesDataSource;     // Источник данных
    private WeatherDataReader noteDataReader;      // Читатель данных

    private LocationManager locationManager;
    private String provider;
    private Set<String> cityList = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            currentCity = savedInstanceState.getString(CITY);
        } else currentCity = "Moscow";

        setContentView(R.layout.activity_main);
        init();
        initDataSource();
        initRetorfit();
        requestRetrofit(currentCity, API_KEY);

    }

    private void requestRetrofitWithCoord(double lat, double lon, String unit, String apiKey) {
        openWeather.loadWeatherTodayWithCoord(lat, lon, unit, apiKey)
                .enqueue(new Callback<WeatherDay>() {
                    @Override
                    public void onResponse(Call<WeatherDay> call, Response<WeatherDay> response) {

                        if (response.body() != null) {
                            WeatherDay data = response.body();
                            valueTemper = (float) ((int) ((data.getTemp()) * 10)) / 10;
                            parseAndSet(data);
                        } else Toast.makeText(MainActivity.this, R.string.Sorry, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<WeatherDay> call, Throwable t) {
                        fail();
                    }
                });
    }

    private void parseAndSet(WeatherDay data) {
        valueWind = data.getWind().getSpeed();
        valueHumidity = data.getHumid();
        valuePress = (int)Math.round(data.getPress());
        currentCity = data.getCity();
        setParams();
        addOrUpdate();
        updateAdapter();
    }

    private void updateAdapter() {
        noteDataReader.open();
        int i = 0;
        String c;
        while (true){
            if (i >= noteDataReader.getCount()) break;
            c = noteDataReader.getPosition(i).getCityName();
            cityList.add(c);
            i++;
        }

        String[] ca = cityList.toArray(new String[cityList.size()]);
        city.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, ca));
    }

    private void fail() {
        temperTextView.setText(getResources().getString(R.string.temper) + " " + "Error");
        windTextView.setText(getResources().getString(R.string.wind) + " " + "Error");
        humidTextView.setText(getResources().getString(R.string.humid) + " " + "Error");
        pressTextView.setText(getResources().getString(R.string.press) + " " +"Error");
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
        openWeather.loadWeatherToday(city, keyApi)
                .enqueue(new Callback<WeatherDay>() {
                    @Override
                    public void onResponse(Call<WeatherDay> call, Response<WeatherDay> response) {

                        if (response.body() != null) {
                            WeatherDay data = response.body();
                            valueTemper = (float) ((int) ((data.getTemp() - 273) * 10)) / 10;
                            parseAndSet(data);
                        } else Toast.makeText(MainActivity.this, R.string.Sorry, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<WeatherDay> call, Throwable t) {
                        fail();
                    }
                });

    }


    private void addOrUpdate() {
        noteDataReader.open(currentCity);
        if (noteDataReader.getPosition(0) == null) {
            notesDataSource.addNote(currentCity, valueTemper, valueWind, valueHumidity, valuePress);
        } else {
            notesDataSource.editNote(noteDataReader.getPosition(0),currentCity, valueTemper, valueWind, valueHumidity, valuePress);
        }
        noteDataReader.close();
    }

    private void setParams() {
        if (isCelsius)setCelsiusParam();
        else setFahrenParam();
         windTextView.setText(getResources().getString(R.string.wind) + " " + valueWind + " mps");
        humidTextView.setText(getResources().getString(R.string.humid) + " " + valueHumidity + " %");
        pressTextView.setText(getResources().getString(R.string.press) + " " + valuePress + " hpa");
        city.setText(currentCity);
    }

    private void init() {
//        currentCity = "Moscow";
//        cityList.add(currentCity);

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
                Toast.makeText(MainActivity.this, "request", Toast.LENGTH_SHORT).show();
                requestRetrofit(currentCity, API_KEY);
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
    }

//    private void getParamFromDB(WeatherNote note) {
//            valueTemper = note.getTemper();
//            valueWind = note.getWind();
//            valueHumidity = note.getHumidity();
//            valuePress = note.getPress();
//            setParams();
//    }

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

        if (id == R.id.nav){
            getPosition();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPosition() {
        // Проверим на пермиссии, и если их нет, запросим у пользователя
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
// Запросим координаты
            requestLocation();
        } else {
// Пермиссии нет, будем запрашивать у пользователя
            requestLocationPermissions();
        }

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

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.for_ten_days){
//            Toast.makeText(this, "create a new fragment", Toast.LENGTH_SHORT).show();
//            // create a new fragment
//
//        }
//        return true;
//    }

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
    private void requestLocation() {
// Если пермиссии все-таки нет - просто выйдем, приложение не имеет смысла
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location!=null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    Toast.makeText(MainActivity.this, lat + " " + lon, Toast.LENGTH_SHORT).show();
                    requestRetrofitWithCoord(lat, lon, UNIT, API_KEY);
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };

// Получим наиболее подходящий провайдер геолокации по критериям
// Но можно и самому назначать, какой провайдер использовать
// В основном это LocationManager.GPS_PROVIDER или LocationManager.NETWORK_PROVIDER
// Но может быть и LocationManager.PASSIVE_PROVIDER (когда координаты уже кто-то недавно получил)
        provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            locationManager.requestSingleUpdate (provider, listener, null);
        }
    }

    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
// Запросим эти две пермиссии у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }


    // Это результат запроса у пользователя пермиссии
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
// Это та самая пермиссия, что мы запрашивали?
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // Пермиссия дана
                requestLocation();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CITY, currentCity);
        super.onSaveInstanceState(outState);
    }
}


