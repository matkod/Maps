package com.example.mateus.maps;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

public class Lugar implements Parcelable {
    public static final Parcelable.Creator<Lugar>
            CREATOR = new Parcelable.Creator<Lugar>() {

        public Lugar createFromParcel(Parcel in) {
            return new Lugar(in);
        }

        public Lugar[] newArray(int size) {
            return new Lugar[size];
        }
    };
    private String nome;
    private double lat;
    private double lng;
    private int raio;
    private boolean isActive;
    private Marker marker;
    private Circle circle;

    public Lugar(String nome, double lat, double lng) {
        this.nome = nome;
        this.lat = lat;
        this.lng = lng;
        marker = null;
        circle = null;
    }

    public Lugar(Parcel p) {
        nome = p.readString();
        lat = p.readDouble();
        lng = p.readDouble();
        raio = p.readInt();
        isActive = p.readByte() != 0;
        marker = null;
        circle = null;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getRaio() {
        return raio;
    }

    public void setRaio(int raio) {
        this.raio = raio;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(raio);
        dest.writeByte((byte) (isActive ? 1 : 0));
    }

    @Override
    public String toString() {
        return nome + "at lat,lng: " + lat + "," + lng;
    }
}
