package com.example.belikov.myapplication.tools;

import android.content.res.Resources;
import android.content.res.TypedArray;

import com.example.belikov.myapplication.R;

import java.util.ArrayList;
import java.util.List;

// Построитель источника данных
public class DataSourceBuilder {
    private List<WeatherCard> dataSource;   // Строим этот источник данных
    String[] temper;
    String[] wind;
    String[] humid;
    String[] press;
//    private Resources resources;    // Ресурсы приложения

    public DataSourceBuilder(String[] temper, String[] wind, String[] humid, String[] press) {
        dataSource = new ArrayList<>(5);
        this.temper = temper;
        this.wind = wind;
        this.humid = humid;
        this.press = press;
//        this.resources = resources;
    }

    // Строим данные
    public List<WeatherCard> build() {


        // Заполнение источника данных
        for (int i = 0; i < temper.length; i++) {
            dataSource.add(new WeatherCard(temper[i], wind[i], humid[i], press[i]));
        }
        return dataSource;
    }

    // Механизм вытаскивания идентификаторов картинок (к сожалению, просто массив не работает)
    // ttps://stackoverflow.com/questions/5347107/creating-integer-array-of-resource-ids
//    private int[] getImageArray(){
//        TypedArray pictures = resources.obtainTypedArray(R.array.pictures);
//        int length = pictures.length();
//        int[] answer = new int[length];
//        for(int i = 0; i < length; i++){
//            answer[i] = pictures.getResourceId(i, 0);
//        }
//        return answer;
//    }
}
