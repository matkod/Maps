package com.example.mateus.maps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListLocationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<Lugar> lugares;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_location);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        lugares = LocationManager.getInstance().getLugares();
        if (savedInstanceState != null) {
            final ArrayList<Lugar> tmp = savedInstanceState.getParcelableArrayList(MainActivity.LIST_LOCATION);
            if (tmp != null)
                LocationManager.getInstance().setLugares(tmp);
        }

        listView = (ListView) findViewById(R.id.listView);

        //final ListAdapter adapter = new LocationArrayAdapter(this, lugares);
        //final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, lugares);
        final LocationBaseAdapter adapter = new LocationBaseAdapter(this, lugares);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("ListLocationActivity", "onSaveInstanceState");
        outState.putParcelableArrayList(MainActivity.LIST_LOCATION, lugares);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        editLocation(position);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.listView) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Lugar obj = (Lugar) lv.getItemAtPosition(acmi.position);

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);

            menu.setHeaderTitle(obj.getNome());

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                editLocation((int) info.id);
                return true;
            case R.id.delete:
                removeLocation((int) info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void editLocation(int position) {
        //Toast.makeText(this, ((Lugar) parent.getItemAtPosition(position)).getNome(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, EditLocationActivity.class);
        intent.putExtra(MainActivity.EDIT_LOCATION, position);
        startActivity(intent);
    }

    private void removeLocation(int position) {
        //Toast.makeText(this, ((Lugar) parent.getItemAtPosition(position)).getNome(), Toast.LENGTH_SHORT).show();
        lugares.remove(position);
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
    }
}
