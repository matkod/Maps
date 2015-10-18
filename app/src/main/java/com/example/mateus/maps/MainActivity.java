package com.example.mateus.maps;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener, LoaderManager.LoaderCallbacks<Cursor>, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public final static String CREATE_LOCATION = "com.example.mateus.maps.CREATE";
    public final static String LIST_LOCATION = "com.example.mateus.maps.LIST";
    public final static String EDIT_LOCATION = "com.example.mateus.maps.EDIT";

    private Marker positionMarker;

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private GoogleMap map;
    private Geocoder geoCoder;

    private ArrayList<Lugar> lugares;

    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geoCoder = new Geocoder(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                setupMap(map);
            }
        });

        if (savedInstanceState != null) {
            Log.d(TAG, "Carregando de savedInstanceState");
            final ArrayList<Lugar> tmp = savedInstanceState.getParcelableArrayList(LIST_LOCATION);
            if (tmp != null)
                LocationManager.getInstance().setLugares(tmp);
        }

        lugares = LocationManager.getInstance().getLugares();

        // First we need to check availability of play services
        if (checkPlayServices()) {
            buildGoogleApiClient();

            createLocationRequest();

            createLocationSettingsRequest();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void setupMap(GoogleMap map) {
        this.map = map;
        this.map.setOnMapLongClickListener(this);
        this.map.setOnMarkerClickListener(this);
        this.map.setMyLocationEnabled(true);
        //map.setTrafficEnabled(true);
        this.map.getUiSettings().setMyLocationButtonEnabled(true);
        this.map.getUiSettings().setMapToolbarEnabled(false);
        this.map.getUiSettings().setZoomControlsEnabled(true);

        resetMap();
    }

    private void resetMap() {
        if (map != null) {
            map.clear();
            drawLocations();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        CursorAdapter suggestionAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);

        //searchView.setSuggestionsAdapter(new PlaceAutocompleteAdapter());
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
        } else if (id == R.id.action_help) {
            return true;
        } else if (id == R.id.action_search) {
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

        Log.d(TAG, "onResume");

        resetMap();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putParcelableArrayList(LIST_LOCATION, lugares);
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

    private void drawLocations() {
        final int size = lugares.size();

        MarkerOptions markerOptions;

        for (int i = 0; i < size; ++i) {
            Lugar l = lugares.get(i);

            markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(l.getLat(), l.getLng()));
            markerOptions.title(l.getNome());

            l.setMarker(map.addMarker(markerOptions));

            int fillColor = Color.argb(150, 235, 235, 235);
            if (l.isActive())
                fillColor = Color.argb(150, 63, 81, 181);

            l.setCircle(map.addCircle(new CircleOptions()
                    .center(new LatLng(l.getLat(), l.getLng()))
                    .radius(l.getRaio())
                    .strokeWidth(0.2f)
                    .fillColor(fillColor)));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLat(), l.getLng()), 15);
            map.animateCamera(cameraUpdate);
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
                map.animateCamera(cameraUpdate);

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

    }

    private void showLocations(Cursor c) {
        MarkerOptions markerOptions;
        LatLng position = null;
        map.clear();
        while (c.moveToNext()) {
            markerOptions = new MarkerOptions();
            position = new LatLng(Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)));
            markerOptions.position(position);
            markerOptions.title(c.getString(0));
            map.addMarker(markerOptions);
        }
        if (position != null) {
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(position);
            map.animateCamera(cameraPosition);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("marker", "click");
        for (int i = 0; i < lugares.size(); ++i) {
            final Lugar l = lugares.get(i);

            if (l.getMarker().equals(marker)) {
                Intent intent = new Intent(this, EditLocationActivity.class);
                intent.putExtra(EDIT_LOCATION, i);
                //intent.putParcelableArrayListExtra(LIST_LOCATION, lugares);
                startActivity(intent);
            }
        }

        return false;
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void createLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        //displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        Toast.makeText(this, "Location changed!", Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        //displayLocation();

        checkLocation();
    }

    /**
     * Method to display the location on UI
     */
    private void displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(latitude, longitude));

            if (positionMarker != null) {
                positionMarker.remove();
            }

            positionMarker = map.addMarker(markerOptions);

            Log.d(TAG, latitude + ", " + longitude);
        } else {
            Log.d(TAG, "(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    private void checkLocation() {
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            for (int i = 0; i < lugares.size(); ++i) {
                if (lugares.get(i).isActive()) {
                    float[] result = new float[3];
                    Location.distanceBetween(latitude, longitude, lugares.get(i).getLat(), lugares.get(i).getLng(), result);

                    if (result[0] < lugares.get(i).getRaio()) {
                        onLocation(lugares.get(i));
                    }
                }
            }
        }
    }

    private void onLocation(Lugar lugar) {
        Log.d(TAG, "Chegou a:" + lugar.getNome());

        Intent intent = new Intent(this, MainActivity.class);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Chegou a " + lugar.getNome())
                .setContentText("boa")
                .setContentIntent(pIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification.build());
    }
}
