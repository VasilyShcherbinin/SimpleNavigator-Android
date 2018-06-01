package com1032.cw2.vs00162.vs00162_assignment2;

/**
 * Created by Vasily on 23/05/2016.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for launching and operating the MapActivity after launch from MainActivity.
 * Contains the main functionality of the app.
 */

public class MapActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, DirectionFinderListener {

    /**
     * Defining local variables.
     */
    private static final int FIND_ADDRESS_REQUEST = 1000;
    private GoogleMap mMap;
    private Button btnCalculateRoute;
    private Button btnFindAddress;
    private EditText startAddress;
    private EditText endAddress;
    private TextView pickerAddress;
    private TextView pickerName;
    private Button satellite;
    private Button roadView;
    private Button terrain;
    private Button hybrid;
    private Button deleteMarkers;
    private Switch trafficView;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    /**
     * Called when MapActivity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Initialising Map and MapActivity components.
        initMap();
        initComponents();

        //All of Listeners for MapActivity components.
        trafficViewListener();
        deleteMarkersListener();
        terrainListener();
        hybridListener();
        roadViewListener();
        satelliteListener();
        calculateRouteListener();
        findAddressListener();
        setOnMapLongClickListener();

        //Connecting to the Google API Client.
        buildGoogleApiClient();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Listener for a long click on the Map.
     */
    private void setOnMapLongClickListener() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                //Draw Marker.
                drawMarker(point);
                //Load into ContentValues Marker parameters - latitude, longitude and zoom.
                ContentValues contentValues = new ContentValues();
                contentValues.put(MarkersDB.LAT, point.latitude);
                contentValues.put(MarkersDB.LNG, point.longitude);
                contentValues.put(MarkersDB.ZOOM, mMap.getCameraPosition().zoom);
                MarkersInsertTask insertTask = new MarkersInsertTask();
                insertTask.execute(contentValues);

                Toast.makeText(getBaseContext(), "Marker is added to Map and DB", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Listener for "Find Address" Button.
     */
    private void findAddressListener() {
        btnFindAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            //Launch Google PlacePicker.
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MapActivity.this), FIND_ADDRESS_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Listener for "Calculate Route" Button.
     */
    private void calculateRouteListener() {
        btnCalculateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calculate path between two points.
                findPath();
                //Hide keyboard.
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }
        });
    }

    /**
     * Listener for "Satellite View" Button.
     */
    private void satelliteListener() {
        satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
    }

    /**
     * Listener for "Road View" Button.
     */
    private void roadViewListener() {
        roadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
    }

    /**
     * Listener for "Hybrid View" Button.
     */
    private void hybridListener() {
        hybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
    }

    /**
     * Listener for "Terrain View" Button.
     */
    private void terrainListener() {
        terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
    }

    /**
     * Listener for "Delete All Markers On Map" Button.
     */
    private void deleteMarkersListener() {
        deleteMarkers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Removing all markers from the Google Map.
                mMap.clear();
                //Creating an instance of MarkersDeleteTask.
                MarkersDeleteTask deleteTask = new MarkersDeleteTask();
                //Deleting all the rows from the SQLite Database table.
                deleteTask.execute();

                Toast.makeText(getBaseContext(), "All markers are removed from DB", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Listener for "Toggle Traffic" Switch.
     */
    private void trafficViewListener() {
        trafficView.setChecked(false);
        trafficView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMap.setTrafficEnabled(true);
                } else {
                    mMap.setTrafficEnabled(false);
                }
            }
        });
    }

    /**
     * Initialize all Activity components.
     */
    private void initComponents() {
        trafficView = (Switch) findViewById(R.id.trafficSwitch);
        deleteMarkers = (Button) findViewById(R.id.deleteMarkers);
        terrain = (Button) findViewById(R.id.btnTerrain);
        roadView = (Button) findViewById(R.id.btnRoadView);
        hybrid = (Button) findViewById(R.id.btnHybrid);
        satellite = (Button) findViewById(R.id.btnSatellite);
        btnCalculateRoute = (Button) findViewById(R.id.btnFindPath);
        btnFindAddress = (Button) findViewById(R.id.placepicker);
        startAddress = (EditText) findViewById(R.id.startAddress);
        endAddress = (EditText) findViewById(R.id.endAddress);
        pickerAddress = (TextView) findViewById(R.id.pickerAddress);
        pickerName = (TextView) findViewById(R.id.pickerName);
    }

    /**
     * Initialize Map.
     */
    private void initMap() {

        //Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        //Showing status
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            //Getting reference to the SupportMapFragment.
            final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            //Getting GoogleMap Object from Fragment.
            mMap = mapFragment.getMap();
            //Invoke LoaderCallbacks to retrieve and draw already saved Markers in map.
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    /**
     * Draw Marker at a point on the Map.
     */
    private void drawMarker(LatLng point) {

        //Creating an instance of MarkerOptions.
        MarkerOptions markerOptions = new MarkerOptions();
        //Setting latitude and longitude of the Marker.
        markerOptions.position(point);
        //Adding Marker to the Google Map.
        mMap.addMarker(markerOptions);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        //Uri to the content provider MarkersContentProvider.
        Uri uri = MarkersContentProvider.CONTENT_URI;
        //Return all the rows from markers table.
        return new android.support.v4.content.CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> arg0, Cursor arg1) {
        int markerCount = 0;
        double lat = 0;
        double lng = 0;
        float zoom = 0;

        //Number of markers available in the SQLite database table.
        markerCount = arg1.getCount();
        //Move the current record pointer to the first row of the table.
        arg1.moveToFirst();

        for (int i = 0; i < markerCount; i++) {

            //Get the latitude.
            lat = arg1.getDouble(arg1.getColumnIndex(MarkersDB.LAT));
            //Get the longitude.
            lng = arg1.getDouble(arg1.getColumnIndex(MarkersDB.LNG));
            //Get the zoom level.
            zoom = arg1.getFloat(arg1.getColumnIndex(MarkersDB.ZOOM));
            LatLng location = new LatLng(lat, lng);

            drawMarker(location);

            arg1.moveToNext();
        }

        if (markerCount > 0) {

            //Moving CameraPosition to last clicked position.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    /**
     * Find path between starting Address and destination Address using DirectionFinder.java class.
     */
    private void findPath() {
        String origin = startAddress.getText().toString();
        String destination = endAddress.getText().toString();

        if (origin.isEmpty()) {
            Toast.makeText(this, R.string.startLocation, Toast.LENGTH_SHORT).show();
            return;
        }

        if (destination.isEmpty()) {
            Toast.makeText(this, R.string.finalDestination, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute when Map is loaded.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setTrafficEnabled(false);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    /**
     * Launch as DirectionFinder starts plotting path.
     */
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    /**
     * Execute when DirectionFinder is successful in finding path.
     */
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            //Move camera to starting location.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //Set travel time in appropriate TextView.
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //Set travel distance in appropriate TextView.
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            //Add origin marker.
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            //Add destination marker.
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(8);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    /**
     * Method to extract address found using PlacePicker Google Places API.
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        if (requestCode == FIND_ADDRESS_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();

            pickerName.setText(name);
            pickerAddress.setText(address);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18));
            }
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private class MarkersInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {
            //Setting up values to insert the marker into the SQLite Database.
            getContentResolver().insert(MarkersContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class MarkersDeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //Deleting all the locations stored in the SQLite database.
            getContentResolver().delete(MarkersContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }
}