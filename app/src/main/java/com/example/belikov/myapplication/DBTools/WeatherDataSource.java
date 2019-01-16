package com.example.belikov.myapplication.DBTools;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import java.io.Closeable;

//  Источник данных, позволяет изменять данные в таблице
// Создает и держит в себе читатель данных
public class WeatherDataSource implements Closeable {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private WeatherDataReader noteDataReader;

    public WeatherDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Открывает базу данных
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        // Создать читателя и открыть его
        noteDataReader = new WeatherDataReader(database);
        noteDataReader.open();
    }

    // Закрыть базу данных
    public void close() {
        noteDataReader.close();
        dbHelper.close();
    }

    // Добавить новую запись
    public WeatherNote addNote(String cityName, float temper, float wind, int humid, int press) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CITY_NAME, cityName);
        values.put(DatabaseHelper.COLUMN_NOTE_TEMPER, temper);
        values.put(DatabaseHelper.COLUMN_NOTE_WIND, wind);
        values.put(DatabaseHelper.COLUMN_NOTE_HUMID, humid);
        values.put(DatabaseHelper.COLUMN_NOTE_PRESS, press);

        // Добавление записи
        long insertId = database.insert(DatabaseHelper.TABLE_NOTES, null,
                values);
        WeatherNote newNote = new WeatherNote();
        newNote.setCityName(cityName);
        newNote.setTemper(temper);
        newNote.setWind(wind);
        newNote.setHumidity(humid);
        newNote.setPress(press);
        newNote.setId(insertId);
        return newNote;
    }

    // Изменить запись
    public void editNote(WeatherNote note, String cityName, float temper, float wind, int humid, int press) {
        ContentValues editedNote = new ContentValues();
        editedNote.put(dbHelper.COLUMN_ID, note.getId());
        editedNote.put(dbHelper.COLUMN_CITY_NAME, cityName);
        editedNote.put(dbHelper.COLUMN_NOTE_TEMPER, temper);
        editedNote.put(dbHelper.COLUMN_NOTE_WIND, wind);
        editedNote.put(dbHelper.COLUMN_NOTE_HUMID, humid);
        editedNote.put(dbHelper.COLUMN_NOTE_PRESS, press);
        // Изменение записи
        database.update(dbHelper.TABLE_NOTES,
                editedNote,
                dbHelper.COLUMN_ID + "=" + note.getId(),
                null);
    }

    // Удалить запись
    public void deleteNote(WeatherNote note) {
        long id = note.getId();
        database.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    // Очистить таблицу
    public void deleteAll() {
        database.delete(DatabaseHelper.TABLE_NOTES, null, null);
    }

    // Вернуть читателя (он потребуется в других местах)
    public WeatherDataReader getNoteDataReader(){
        return noteDataReader;
    }
}

