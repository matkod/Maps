package com.example.mateus.maps;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LocationArrayAdapter extends ArrayAdapter<Lugar> {

    HashMap<Lugar, Integer> mIdMap = new HashMap<Lugar, Integer>();

    public LocationArrayAdapter(Context context, ArrayList<Lugar> values) {
        super(context, R.layout.location_list_layout, values);

        for (int i = 0; i < values.size(); ++i) {
            mIdMap.put(values.get(i), i);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.location_list_layout, parent, false);

        TextView textView1 = (TextView) rowView.findViewById(R.id.tvLocationFirst);
        textView1.setText(getItem(position).getNome());

        TextView textView2 = (TextView) rowView.findViewById(R.id.tvLocationSecond);
        textView2.setText(getItem(position).getRaio() + "m");

        Switch switch1 = (Switch) rowView.findViewById(R.id.switch1);
        switch1.setChecked(getItem(position).isActive());

        return rowView;
    }

   /* @Override
    public long getItemId(int position) {
        Lugar item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0) {
        return true;
    } */
}
