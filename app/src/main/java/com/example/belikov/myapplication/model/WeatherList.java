package com.example.belikov.myapplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherList {

    @SerializedName("dt")
    @Expose
    private long dt;
    @SerializedName("mainList")
    @Expose
    private MainList mainList;
    @SerializedName("weather")
    @Expose
    private Weather[] weather;
    @SerializedName("clouds")
    @Expose
    private Clouds clouds;
    @SerializedName("wind")
    @Expose
    private Wind wind;

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public MainList getMainList() {
        return mainList;
    }

    public void setMainList(MainList mainList) {
        this.mainList = mainList;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getDt_text() {
        return dt_text;
    }

    public void setDt_text(String dt_text) {
        this.dt_text = dt_text;
    }

    @SerializedName("sys")

    @Expose
    private Sys sys;
    @SerializedName("dt_text")
    @Expose
    private String dt_text;

}
