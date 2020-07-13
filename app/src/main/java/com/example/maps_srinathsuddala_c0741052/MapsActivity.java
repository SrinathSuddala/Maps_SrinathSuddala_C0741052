package com.example.maps_srinathsuddala_c0741052;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowLongClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private ArrayList<LatLng> latLngs;
    private ArrayList<PolylineOptions> polylineOptions;
    private String[] title = {"A", "B", "C", "D"};
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        latLngs = new ArrayList<>();
        polylineOptions = new ArrayList<>();
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnPolylineClickListener(this);
        mMap.setOnPolygonClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
//            try {
//                List<Address> listAddresses = geocoder.getFromLocation(latitude,
//                        longitude, 1);
//                if (null != listAddresses && listAddresses.size() > 0) {
//                    String state = listAddresses.get(0).getAdminArea();
//                    String country = listAddresses.get(0).getCountryName();
//                    String subLocality = listAddresses.get(0).getSubLocality();
//                    markerOptions.title("" + latLng + "," + subLocality + "," + state
//                            + "," + country);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
//                    this);
//        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (latLngs.size() < 4) {
            addMarker(latLng);
            if (latLngs.size() != 0) {
                drawLine(latLngs.get(latLngs.size() - 1), latLng);
            }
            latLngs.add(latLng);
            if (latLngs.size() == 4) {
                drawLine(latLngs.get(0), latLng);
                drawPolygon();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        double distance = getDistance(polyline.getPoints().get(0), polyline.getPoints().get(polyline.getPoints().size() - 1)) / 100;
        Toast.makeText(this, "Distance = " + distance, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        double distance = (getDistance(latLngs.get(0), latLngs.get(1)) + getDistance(latLngs.get(1), latLngs.get(2))
                + getDistance(latLngs.get(2), latLngs.get(3)) + getDistance(latLngs.get(3), latLngs.get(0))) / 100;
        Toast.makeText(this, "Distance = " + distance, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        removeMarker(marker);
        if (latLngs.size() > 0) {

        }
    }
    private String getTitle(LatLng latLng) {
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (null != listAddresses && listAddresses.size() > 0) {
                String thoroughfare = listAddresses.get(0).getThoroughfare();
                String subThoroughfare = listAddresses.get(0).getSubThoroughfare();
                String postalCode = listAddresses.get(0).getPostalCode();
                if (thoroughfare != null && subThoroughfare != null && postalCode != null) {
                    return thoroughfare.concat(", ").concat(subThoroughfare).concat(", ").concat(postalCode);
                } else if (thoroughfare == null && subThoroughfare != null && postalCode != null) {
                    return subThoroughfare.concat(", ").concat(postalCode);
                } else if (thoroughfare != null && subThoroughfare == null && postalCode != null) {
                    return thoroughfare.concat(", ").concat(postalCode);
                } else if (thoroughfare != null && subThoroughfare != null) {
                    return thoroughfare.concat(", ").concat(subThoroughfare);
                } else {
                    return "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getSnippet(LatLng latLng) {
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (null != listAddresses && listAddresses.size() > 0) {
                String city = listAddresses.get(0).getAddressLine(0);
                String state = listAddresses.get(0).getAddressLine(1);
                if (city != null && state != null) {
                    return city.concat(", ").concat(state);
                } else if (city == null && state != null) {
                    return state;
                } else if (city != null) {
                    return city;
                } else {
                    return "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void addMarker(LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getTitle(latLng))
                .snippet(getSnippet(latLng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }

    private void removeMarker(Marker marker) {
        marker.remove();
        latLngs.remove(marker.getPosition());
    }

    private void drawLine(LatLng latLng1, LatLng latLng2) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(latLng1, latLng2)
                .width(5)
                .color(Color.RED);
        Polyline polyline = mMap.addPolyline(polylineOptions);
        polyline.setClickable(true);
        this.polylineOptions.add(polylineOptions);
    }

    private void drawPolygon() {
        if (latLngs.size() == 4) {
            mMap.addPolygon(new PolygonOptions()
                    .add(latLngs.get(0), latLngs.get(1), latLngs.get(2), latLngs.get(3))
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(getResources().getColor(R.color.transparent_green)));
        }
    }

