package com.example.belikov.myapplication.DBTools;

// Класс-отражение строк из таблицы
public class WeatherNote {
    private long id;
    private String cityName;
    private float temper;
    private float wind;
    private int humidity;
    private int press;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public float getTemper() {
        return temper;
    }

    public void setTemper(float temper) {
        this.temper = temper;
    }

    public float getWind() {
        return wind;
    }

    public void setWind(float wind) {
        this.wind = wind;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPress() {
        return press;
    }

    public void setPress(int press) {
        this.press = press;
    }
}

