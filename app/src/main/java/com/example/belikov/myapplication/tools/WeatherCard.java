package com.example.belikov.myapplication.tools;

public class WeatherCard {
    private String date;
    private String temper; // описание
    private String wind;
    private String humid;
    private String press;


    public String getDate() {
        return date;
    }

    public WeatherCard(String date, String temper, String wind, String humid, String press){
        this.date = date;

        this.temper = temper;
        this.wind = wind;
        this.humid = humid;
        this.press = press;
    }

    public String getTemper() {
        return temper;
    }

    public String getWind() {
        return wind;
    }

    public String getHumid() {
        return humid;
    }

    public String getPress() {
        return press;
    }
}

