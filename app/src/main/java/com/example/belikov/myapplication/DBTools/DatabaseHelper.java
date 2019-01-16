package com.example.belikov.myapplication.DBTools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Класс установки базы данных: создать базу данных, если ее нет; проапгрейдить ее
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db"; // Название БД
    public static final int DATABASE_VERSION = 2; // Версия базы данных
    static final String TABLE_NOTES = "weather"; // Название таблицы в БД
    // Названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CITY_NAME = "city_name";
    public static final String COLUMN_NOTE_TEMPER = "temper";
    public static final String COLUMN_NOTE_WIND = "wind";
    public static final String COLUMN_NOTE_HUMID = "humid";
    public static final String COLUMN_NOTE_PRESS = "press";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Вызывается при попытке доступа к базе данных, когда она еще не создана
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NOTES + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_CITY_NAME + " TEXT," + COLUMN_NOTE_TEMPER + " REAL," + COLUMN_NOTE_WIND + " REAL," +
                COLUMN_NOTE_HUMID + " INTEGER," + COLUMN_NOTE_PRESS + " INTEGER);");
    }

    // Вызывается, когда необходимо обновление базы данных
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        if ((oldVersion == 1) && (newVersion == 2)) {
            String upgradeQuery = "ALTER TABLE " + TABLE_NOTES + " ADD COLUMN " + COLUMN_CITY_NAME + " TEXT";
            db.execSQL(upgradeQuery);
        }
    }
}

