package com.example.belikov.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.List;

public class WeatherDay {
    public class WeatherMain {
        Double temp;
        Double temp_min;
        Double temp_max;
        Integer humidity;
        Double pressure;
    }

    public class WeatherDescription {
        String icon;
    }

    @SerializedName("main")
    private WeatherMain main;

    @SerializedName("weather")
    private List<WeatherDescription> desctiption;

    @SerializedName("name")
    private String city;

    @SerializedName("dt")
    private long timestamp;

    @SerializedName("wind")
    private Wind wind;


    public Wind getWind() {
        return wind;
    }

    public WeatherDay(WeatherMain main, List<WeatherDescription> desctiption) {
        this.main = main;
        this.desctiption = desctiption;
    }

    public Calendar getDate() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp * 1000);
        return date;
    }

    public String getTemp() { return String.valueOf(main.temp); }

    public String getTempMin() { return String.valueOf(main.temp_min); }

    public String getTempMax() { return String.valueOf(main.temp_max); }

    public String getHumid() { return String.valueOf(main.humidity);}

    public String getPress() { return String.valueOf(main.pressure);}

    public String getTempInteger() { return String.valueOf(main.temp.intValue()); }

    public String getTempWithDegree() { return String.valueOf(main.temp.intValue()) + "\u00B0"; }

    public String getCity() { return city; }

    public String getIcon() { return desctiption.get(0).icon; }

    public String getIconUrl() {
        return "http://openweathermap.org/img/w/" + desctiption.get(0).icon + ".png";
    }
}
