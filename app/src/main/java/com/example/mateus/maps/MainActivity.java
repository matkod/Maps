package com.example.mateus.maps;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

//import android.widget.SearchView;
//import android.app.Activity


public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener, LoaderManager.LoaderCallbacks<Cursor>, GoogleMap.OnMarkerClickListener {

    public final static String CREATE_LOCATION = "com.example.mateus.maps.LOCATION";
    public final static String LOCATION_LIST = "com.example.mateus.maps.LIST";
    public final static String EDIT_LOCATION = "com.example.mateus.maps.EDIT";

    private GoogleMap gm;
    private Geocoder geoCoder;

    private ArrayList<Lugar> lugares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);

        geoCoder = new Geocoder(this);

        gm = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment)).getMap();
        gm.setOnMapLongClickListener(this);
        gm.setMyLocationEnabled(true);

        if (savedInstanceState != null) {
            Log.d("MainActivity", "Carregando de savedInstanceState");
            final ArrayList<Lugar> tmp = savedInstanceState.getParcelableArrayList(LOCATION_LIST);
            LocationManager.getInstance().setLugares(tmp);
        }

        lugares = LocationManager.getInstance().getLugares();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        CursorAdapter suggestionAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);

        //searchView.setSuggestionsAdapter(suggestionAdapter);
        //searchView.setSuggestionsAdapter(new SuggestionsAdapter(getApplication(), null));

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
        } else if (id == R.id.search) {
            //onSearchRequested();
            return true;
        } else if (id == R.id.action_favorites) {
            Intent intent = new Intent(this, ListLocationActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("MainActivity", "onResume");

        clearLocations();
        drawLocations();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("MainActivity", "salvando...");
        outState.putParcelableArrayList(LOCATION_LIST, lugares);
    }

    private void getPlace(String query) {
        Bundle data = new Bundle();
        data.putString("query", query);
        getLoaderManager().restartLoader(1, data, this);
    }

    private void doSearch(String query) {
        Bundle data = new Bundle();
        data.putString("query", query);
        getLoaderManager().restartLoader(0, data, this);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            doSearch(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    public void drawLocations() {
        final int size = lugares.size();

        MarkerOptions markerOptions;

        for (int i = 0; i < size; ++i) {
            Lugar l = lugares.get(i);

            markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(l.getLat(), l.getLng()));
            markerOptions.title(l.getNome());
            l.setMarker(gm.addMarker(markerOptions));

            l.setCircle(gm.addCircle(new CircleOptions()
                    .center(new LatLng(l.getLat(), l.getLng()))
                    .radius(l.getRaio())
                    .strokeWidth(1)
                    .fillColor(Color.argb(120, 250, 250, 0))));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLat(), l.getLng()), 15);
            gm.animateCamera(cameraUpdate);
        }
    }

    private void clearLocations() {
        int size = lugares.size();

        for (int i = 0; i < size; ++i) {
            Lugar l = lugares.get(i);
            if (l.getMarker() != null)
                l.getMarker().remove();
            if (l.getCircle() != null)
                l.getCircle().remove();
        }
    }

    private void doSearch2(String query) {
        Log.d("ACTION_SEARCH", query);

        try {
            List<Address> addresses = geoCoder.getFromLocationName(query, 5);

            if (addresses.size() > 0) {
                    /*Barcode.GeoPoint p = new Barcode.GeoPoint(1, (addresses.get(0).getLatitude() * 1E6),
                             (addresses.get(0).getLongitude() * 1E6));

                    controller.animateTo(p);
                    controller.setZoom(12);

                    MapOverlay mapOverlay = new MapOverlay();
                    List<Overlay> listOfOverlays = map.getOverlays();
                    listOfOverlays.clear();
                    listOfOverlays.add(mapOverlay);

                    map.invalidate();
                    txtsearch.setText(""); */
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()), 10);
                gm.animateCamera(cameraUpdate);

            } else {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Google Map");
                adb.setMessage("Please Provide the Proper Place");
                adb.setPositiveButton("Close", null);
                adb.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        final LatLng fLatLng = latLng;

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.create_new_location);
        adb.setMessage(R.string.create_new_location_question);
        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                createNewLocation(fLatLng);
            }
        });
        adb.setNegativeButton(R.string.no, null);
        adb.show();
    }

    private void createNewLocation(LatLng latLng) {
        final Lugar l = new Lugar("", latLng.latitude, latLng.longitude);

        lugares.add(l);

        Intent intent = new Intent(this, EditLocationActivity.class);
        intent.putExtra(EDIT_LOCATION, lugares.size() - 1);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
        CursorLoader cLoader = null;
        if (arg0 == 0)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{query.getString("query")}, null);
        else if (arg0 == 1)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{query.getString("query")}, null);
        return cLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        showLocations(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }

    private void showLocations(Cursor c) {
        MarkerOptions markerOptions;
        LatLng position = null;
        gm.clear();
        while (c.moveToNext()) {
            markerOptions = new MarkerOptions();
            position = new LatLng(Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)));
            markerOptions.position(position);
            markerOptions.title(c.getString(0));
            gm.addMarker(markerOptions);
        }
        if (position != null) {
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(position);
            gm.animateCamera(cameraPosition);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        for (int i = 0; i < lugares.size(); ++i) {
            final Lugar l = lugares.get(i);

            if (l.getMarker().equals(marker)) {
                Intent intent = new Intent(this, EditLocationActivity.class);
                intent.putExtra(EDIT_LOCATION, i);
                //intent.putParcelableArrayListExtra(LOCATION_LIST, lugares);
                startActivity(intent);
            }
        }

        return false;
    }
}
