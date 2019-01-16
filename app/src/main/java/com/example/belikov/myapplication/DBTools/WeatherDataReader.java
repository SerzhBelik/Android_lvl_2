package com.example.belikov.myapplication.DBTools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.Closeable;

// Читатель источника данных на основе курсора
// Этот класс был вынесен из NoteDataSource, чтобы разгрузить его ответственности
public class WeatherDataReader implements Closeable {

    private Cursor cursor;              // Курсор (фактически, подготовленный запрос),
    // но сами данные подсчитываются только по необходимости
    private SQLiteDatabase database;

    private String[] notesAllColumn = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_CITY_NAME,
            DatabaseHelper.COLUMN_NOTE_TEMPER,
            DatabaseHelper.COLUMN_NOTE_WIND,
            DatabaseHelper.COLUMN_NOTE_HUMID,
            DatabaseHelper.COLUMN_NOTE_PRESS
    };

    public WeatherDataReader(SQLiteDatabase database){
        this.database = database;
    }

    // Подготовить к чтению таблицу
    public void open(String city){
        query(city);
        cursor.moveToFirst();
    }

    public void open(){
        query();
        cursor.moveToFirst();
    }

    public void close(){
        cursor.close();
    }

    // Перечитать таблицу (если точно – обновить курсор)
    public void Refresh(String city){
        int position = cursor.getPosition();
        query(city);
        cursor.moveToPosition(position);
    }

    // Создание запроса на курсор
    private void query(String city){
        cursor = database.query(DatabaseHelper.TABLE_NOTES,
                notesAllColumn, "city_name = " + "'" + city + "'", null, null, null, null);
    }

    private void query(){
        cursor = database.query(DatabaseHelper.TABLE_NOTES,
                notesAllColumn, null, null, null, null, null);
    }

    // Прочитать данные по определенной позиции
    public WeatherNote getPosition(int position){
        cursor.moveToPosition(position);
        return cursorToNote();
    }

    // Получить количество строк в таблице
    public int getCount(){
        return cursor.getCount();
    }

    // Преобразователь данных курсора в объект
    private WeatherNote cursorToNote() {
        WeatherNote note = new WeatherNote();
        if (getCount() == 0) return null;
        note.setId(cursor.getLong(0));
        note.setCityName(cursor.getString(1));
        note.setTemper(cursor.getFloat(2));
        note.setWind(cursor.getFloat(3));
        note.setHumidity(cursor.getInt(4));
        note.setPress(cursor.getInt(5));
        return note;
    }
}

