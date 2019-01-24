package com.example.belikov.myapplication.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherRequest {
    @SerializedName("city")
    @Expose
    private City city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("message")
    @Expose
    private float message;
    @SerializedName("cod")
    @Expose
    private int cod;
    @SerializedName("cnt")
    @Expose
    private int cnt;
    @SerializedName("list")
    @Expose
    private WeatherList list;


    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public float getMessage() {
        return message;
    }

    public void setMessage(float message) {
        this.message = message;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public WeatherList getList() {
        return list;
    }

    public void setList(WeatherList list) {
        this.list = list;
    }
}

