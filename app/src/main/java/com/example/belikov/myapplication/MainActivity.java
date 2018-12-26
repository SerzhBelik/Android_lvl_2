package com.example.belikov.myapplication;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import static android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE;
import static android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Locale locale;

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private Sensor humiditySensor;

    private TextView temperTextView;
    private TextView humidTextView;
    private TextView windTextView;
    private TextView pressTextView;
    private EditText editTextYears;

    private static final String CELSIUS = " °C";
    private static final String FAHRENHEIT = " °F";
    private static final String PERCENT = " %";
    public final static String BROADCAST_ACTION = "com.example.belikov.myapplication";
    public static final String TEMPER = "temperature";
    public static final String WIND = "wind";
    public static final String HUMIDITY = "humid";
    public static final String PRESSURE = "press";
    public static final String YEARS = "years";


    private boolean isCelsius = true;

    private float valueTemper;
    private int years;

    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        temperatureSensor = sensorManager.getDefaultSensor(TYPE_AMBIENT_TEMPERATURE);
        humiditySensor = sensorManager.getDefaultSensor(TYPE_RELATIVE_HUMIDITY);

        sensorManager.registerListener(listenerTemper, temperatureSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listenerHumid, humiditySensor,
                SensorManager.SENSOR_DELAY_NORMAL);


        temperTextView = findViewById(R.id.temperature);
        humidTextView = findViewById(R.id.humidity);
        windTextView = findViewById(R.id.wind);
        pressTextView = findViewById(R.id.pressure);
        editTextYears = findViewById(R.id.years_value);

//        CardView cardView1 = findViewById(R.id.cardView1);
//        CardView cardView2 = findViewById(R.id.cardView2);
//        CardView cardView3 = findViewById(R.id.cardView3);

//        registerForContextMenu(cardView1);
//        registerForContextMenu(cardView2);
//        registerForContextMenu(cardView3);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                //FIXME
                valueTemper = intent.getIntExtra(TEMPER,0);
                setParams(intent);
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);

        startService(new Intent(MainActivity.this, MyService.class));

        Button ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FIXME
                years = Integer.parseInt(editTextYears.getText().toString());
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra(YEARS, years);
                startActivity(intent);
            }
        });
    }

    private void setParams(Intent intent) {
        showTemper();
        showHumid(intent);
        showWind(intent);
        showPress(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerTemper, temperatureSensor);
        sensorManager.unregisterListener(listenerHumid, humiditySensor);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.find_city) {
            Toast.makeText(this, "find_city", Toast.LENGTH_SHORT).show();
            findCity();
            return true;
        }

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.for_ten_days){
            Toast.makeText(this, "create a new fragment", Toast.LENGTH_SHORT).show();
            // create a new fragment

        }
        return true;
    }

    public void onAvatarClick(View view){
        Toast.makeText(this, "avatar", Toast.LENGTH_SHORT).show();
        setAnAvatar();
        //FIXME
    }

    private void setAnAvatar() {
        //FIXME
    }

    private void setLocale(String lang){
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }

    private void findCity() {
        //FIXME
    }

    SensorEventListener listenerTemper = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            valueTemper = event.values[0];
            showTemper();
        }
    };

    SensorEventListener listenerHumid = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            showHumidSensors(event);
        }
    };

    private void showTemper(){
        if (isCelsius){
            setCelsiusParam();
        } else setFahrenParam();

    }

    private void showHumidSensors(SensorEvent event){
        humidTextView.setText(humidTextView.getText().toString() + " " + event.values[0] + PERCENT);
    }

    private void showHumid(Intent intent){
        humidTextView.setText(getResources().getString(R.string.humid) + " " + intent.getIntExtra(HUMIDITY, 0) + PERCENT);
    }

    private void showWind(Intent intent) {
        windTextView.setText(getResources().getString(R.string.wind) + " " + intent.getFloatExtra(WIND, 0));
    }

    private void showPress(Intent intent) {
        pressTextView.setText(getResources().getString(R.string.press) + " " + intent.getIntExtra(PRESSURE, 0));
    }

    private void setCelsiusParam(){
        temperTextView.setText(getResources().getString(R.string.temper) + " " + valueTemper + CELSIUS);
    }

    private void setFahrenParam(){
        temperTextView.setText(getResources().getString(R.string.temper) + " " + ((valueTemper*9/5+32)) + FAHRENHEIT);
    }



}


