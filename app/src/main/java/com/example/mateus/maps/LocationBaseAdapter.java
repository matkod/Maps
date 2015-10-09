package com.example.mateus.maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;


public class LocationBaseAdapter extends BaseAdapter {
    private final ArrayList<Lugar> list;

    private final Context context;

    public LocationBaseAdapter(Context context, ArrayList<Lugar> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Lugar getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        final Lugar lugar = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.location_list_layout, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.position = position;
            viewHolder.textView1 = (TextView) convertView.findViewById(R.id.tvLocationFirst);
            viewHolder.textView2 = (TextView) convertView.findViewById(R.id.tvLocationSecond);
            viewHolder.switch1 = (Switch) convertView.findViewById(R.id.switch1);

            viewHolder.switch1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lugar.setIsActive(!lugar.isActive());
                    viewHolder.switch1.setChecked(lugar.isActive());
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (lugar != null) {
            viewHolder.textView1.setText(lugar.getNome());
            viewHolder.textView2.setText(String.format("%1$d m", lugar.getRaio()));
            viewHolder.switch1.setChecked(lugar.isActive());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView textView1;
        TextView textView2;
        Switch switch1;
        int position;
    }
}
