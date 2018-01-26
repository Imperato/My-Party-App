package com.example.michele.myparty_new;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pojo.PartyMarker;

public class MapFragment extends Fragment implements LocationListener, View.OnClickListener {

    private String URL;
    MapView mMapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Marker marker;
    private EditText locationSearch;
    private double latitude;
    private double longitude;
    private boolean locationResult;
    private LatLng latLng;
    private Button searchButton;
    ArrayList<PartyMarker> partyMarkerList = new ArrayList<>();
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        URL = getString(R.string.server_address) +"parties/";
        mMapView = v.findViewById(R.id.mapView);
        locationSearch = v.findViewById(R.id.edit_text_search);
        searchButton = v.findViewById(R.id.btn_search);
        searchButton.setOnClickListener(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            // Initialize the map
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationResult = ((HomeActivity) getActivity()).checkLocationPermission();
        if (locationResult)
            actionGetParties();
        return v;
    }

    // Handle the map
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        if(v==searchButton)
            onMapSearch();
    }

    // Get the parties with location set and add them as markers into the map
    public void actionGetParties() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Process the JSON
                        try{
                            if (!partyMarkerList.isEmpty())
                                partyMarkerList.clear();
                            // Loop through the array elements
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                JSONObject party = response.getJSONObject(i);
                                // Get the current party (json object) data
                                String partyId = party.getString("id");
                                String partyName = party.getString("name");
                                String partyOrgName = party.getString("organizerName");
                                String partyDate = party.getString("date");
                                double latitude = party.getDouble("latitude");
                                double longitude = party.getDouble("longitude");
                                // Create and add to the list the new element if its location has been set
                                if (latitude != 0 || longitude != 0) {
                                    PartyMarker pm = new PartyMarker(partyId, partyName, partyOrgName, partyDate, latitude, longitude);
                                    partyMarkerList.add(pm);
                                }
                            }
                            mMapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap mMap) {
                                    googleMap = mMap;
                                    // For showing a move to my location button
                                    // In order to get the position
                                    googleMap.setMyLocationEnabled(true);
                                    // Get LocationManager service
                                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE); // Ottiene il servizio LocationManager
                                    if (locationManager != null) {
                                        // Listener for location updates from GPS
                                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, MapFragment.this);
                                        // Listener for location updates from Internet
                                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, MapFragment.this);
                                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, MapFragment.this);
                                    }
                                    for (int i=0; i<partyMarkerList.size(); i++) {
                                        PartyMarker pm = partyMarkerList.get(i);
                                        LatLng latLng = new LatLng(pm.getLatitude(), pm.getLongitude());
                                        // Every marker has position, title (partyName), subtitle (partyOrganizerName) and tag (partyId)
                                        Marker myMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title(pm.getPartyName()).snippet(pm.getOrganizerName()));
                                        myMarker.setTag(pm.getId());
                                    }
                                    // Listener for click on the description of the marker
                                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
                                            String id = (String) marker.getTag();
                                            ((HomeActivity) getActivity()).setItemId(id);
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.frame, PartyFromMapFragment.newInstance(), "party_from_map")
                                                    .commit();
                                        }
                                    });
                                }
                            });
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

    // When the user search an address
    public void onMapSearch() {
        // Remove the previous marker if there is
        if (marker != null)
            marker.remove();
        // Get the address
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            // Search for the address
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList.isEmpty()) {
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle(R.string.no_location_found_title);
                b.setMessage(R.string.no_location_found);
                b.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                // Non fa niente
                            }
                        });
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.show();
            }
            else {
                Address address = addressList.get(0);
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                // This marker is blue to distinguish it from the other markers
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        }
    }

    // What to do when location changes
    @Override
    public void onLocationChanged(Location location) {
        // Get the new location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // Place camera on the new position with zoom = 10
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        googleMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    // Handle the detection of the position
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

}
