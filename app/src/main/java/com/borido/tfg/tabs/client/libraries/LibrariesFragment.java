package com.borido.tfg.tabs.client.libraries;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.borido.tfg.models.Library;
import com.borido.tfg.R;
import com.borido.tfg.tabs.admin.libraries.ControlLibraryActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LibrariesFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private double mLatitude = 0.0, mLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_tabs_fragment_libraries, container, false);

        /*----------------------------------------------- GOOGLE MAPS ------------------------------------------------------------------*/

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(16000);
        locationRequest.setFastestInterval(8000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();

                        LatLng posicion_actualizada = new LatLng(mLatitude, mLongitude);
                        mMap.addMarker(new MarkerOptions().position(posicion_actualizada).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion_actualizada));
                    }
                }
            }
        };

        requestLocations();

        controlData();

        return view;
    }

    private void requestLocations() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        } else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void removeLocations() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng locationTorrent = new LatLng(39.429486289316145, -0.47512006880151403);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationTorrent,14));

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getActivity(), LibraryDetailActivity.class);
                intent.putExtra("library", (Serializable) marker.getTag());
                startActivity(intent);
            }
        });
    }

    /*------------------------------------------ DATA --------------------------------------------------*/

    private void controlData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference librariesRef = database.getReference("libraries");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.i("info", "onChildAdded:" + dataSnapshot.getValue());
                loadData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.i("info", "onChildChanged:" + dataSnapshot.getValue());
                mMap.clear();
                controlData();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i("info", "onChildRemoved:" + dataSnapshot.getValue());
                mMap.clear();
                controlData();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.i("info", "onChildMoved:" + dataSnapshot.getValue());
                mMap.clear();
                controlData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("info", "postComments:onCancelled", databaseError.toException());
            }
        };
        librariesRef.addChildEventListener(childEventListener);
    }

    private void loadData(DataSnapshot dataSnapshot) {
        Library library = dataSnapshot.getValue(Library.class);

        LatLng libraryLocation = new LatLng(Double.parseDouble(library.getLatitude()), Double.parseDouble(library.getLongitude()));
        Marker markerLibrary = mMap.addMarker(new MarkerOptions().position(libraryLocation).title(library.getName()).snippet((String) getText(R.string.snippet_maps_libraries)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_library128px)));
        markerLibrary.setTag(library);
    }


}