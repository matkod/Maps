package com.example.mateus.maps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListLocationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<Lugar> lugares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_location);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        //lugares = getIntent().getParcelableArrayListExtra(MainActivity.LOCATION_LIST);
        lugares = LocationManager.getInstance().getLugares();
        if (lugares == null) {
            lugares = new ArrayList<>();
        }

        final ListView listview = (ListView) findViewById(R.id.listView);

        //final ListAdapter adapter = new LocationArrayAdapter(this, lugares);
        //final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, lugares);
        final LocationBaseAdapter adapter = new LocationBaseAdapter(this, lugares);
        listview.setAdapter(adapter);
        //listview.setClickable(true);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, ((Lugar) parent.getItemAtPosition(position)).getNome(), Toast.LENGTH_SHORT).show();
    }

}
