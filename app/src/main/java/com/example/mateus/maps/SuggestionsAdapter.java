package com.example.mateus.maps;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SuggestionsAdapter extends CursorAdapter {
    public SuggestionsAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // I modified the layout of search_item.xml
        View view = inflater.inflate(R.layout.search_item, parent, false);

        Log.d("TESTE", cursor.getString(0));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.item);

        tv.setText(cursor.getString(0));
        Log.d("TESTE", cursor.getString(0));
    }
}
