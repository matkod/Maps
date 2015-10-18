package com.example.mateus.maps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String NAME = "teste";
    private static final int VERSION = 1;

    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE lugares (" +
                    "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "lat REAL NOT NULL, " +
                    "lng REAL NOT NULL, " +
                    "active BOOLEAN NOT NULL, " +
                    "raio INTEGER NOT NULL, " +
                    ");";


    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
