package com.example.mateus.maps;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseLugar {
    private SQLiteDatabase db;

    public DatabaseLugar(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public void inserir(Lugar lugar) {
        ContentValues cv = new ContentValues();
        cv.put("nome", lugar.getNome());
        cv.put("lat", lugar.getLat());
        cv.put("lng", lugar.getLng());
        cv.put("active", lugar.isActive());
        cv.put("raio", lugar.getRaio());

        lugar.setId((int) (db.insert("lugares", null, cv)));
    }

    public void atualizar(Lugar lugar) {
        ContentValues cv = new ContentValues();
        cv.put("nome", lugar.getNome());
        cv.put("lat", lugar.getLat());
        cv.put("lng", lugar.getLng());
        cv.put("active", lugar.isActive());
        cv.put("raio", lugar.getRaio());

        db.update("lugares", cv, "_id = " + lugar.getId(), null);
    }

    public void excluir(Lugar lugar) {
        db.delete("lugares", "_id = " + lugar.getId(), null);
    }

    public ArrayList<Lugar> buscar() {
        ArrayList<Lugar> lugares = new ArrayList<Lugar>();

        String[] colunas = new String[]{"_id", "nome", "lat", "lng", "active", "raio"};

        Cursor c = db.query("lugares", colunas, null, null, null, null, "nome");

        if (c.getCount() > 0) {
            c.moveToFirst();

            do {

                Lugar l = new Lugar();
                l.setId(c.getInt(0));
                l.setNome(c.getString(1));
                l.setLat(c.getDouble(2));
                l.setLng(c.getDouble(3));
                l.setIsActive(c.getShort(4) == 1);
                l.setRaio(c.getInt(5));

                lugares.add(l);

            } while (c.moveToNext());
        }

        return lugares;
    }
}
