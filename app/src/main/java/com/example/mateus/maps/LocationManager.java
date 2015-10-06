package com.example.mateus.maps;


import java.util.ArrayList;

public class LocationManager {
    private static final LocationManager ourInstance = new LocationManager();

    private ArrayList<Lugar> lugares;

    private LocationManager() {
        lugares = new ArrayList<>();
    }

    public static LocationManager getInstance() {
        return ourInstance;
    }

    public ArrayList<Lugar> getLugares() {
        return lugares;
    }

    public void setLugares(ArrayList<Lugar> lugares) {
        this.lugares = lugares;
    }
}
