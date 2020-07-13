package com.example.maps_srinathsuddala_c0741052;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
        LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowLongClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnMarkerDragListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private ArrayList<LatLng> latLngs;
    private ArrayList<Polyline> polylines;
    private String[] title = {"A", "B", "C", "D"};
    private Geocoder geocoder;
    private int markerPosition, polyLinePosition;

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
        polylines = new ArrayList<>();
        latLngs = new ArrayList<>();
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnPolylineClickListener(this);
        mMap.setOnPolygonClickListener(this);
        mMap.setOnMarkerDragListener(this);
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
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
            geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
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
        Toast.makeText(this, "Polyline Distance = " + distance, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        double distance = (getDistance(latLngs.get(0), latLngs.get(1)) + getDistance(latLngs.get(1), latLngs.get(2))
                + getDistance(latLngs.get(2), latLngs.get(3)) + getDistance(latLngs.get(3), latLngs.get(0))) / 100;
        Toast.makeText(this, "Polygon Distance = " + distance, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
/*        markerPosition = 0;
        polyLinePosition = 0;
        for (int i = 0; i < latLngs.size(); i++) {
            if (latLngs.get(i).equals(marker.getPosition())) {
                markerPosition = i;
            }
        }
        for (int i = 0; i < polylines.size(); i++) {
            if (polylines.get(i).getPoints().get(0).equals(marker.getPosition()) ||
                    polylines.get(i).getPoints().get(polylines.get(i).getPoints().size() - 1).equals(marker.getPosition())) {
                polyLinePosition = i;
                polylines.get(i).remove();
            }
        }*/
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
/*        latLngs.set(markerPosition, marker.getPosition());
        if (latLngs.size() >= markerPosition + 1) {
            drawLine(latLngs.get(markerPosition - 1), latLngs.get(markerPosition));
            drawLine(latLngs.get(markerPosition), latLngs.get(markerPosition + 1));
        }*/

   /*     for (int i = 0; i < polylines.size(); i++) {
            polylines.get(i).remove();
        }*/
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
       /* removeMarker(marker);
        latLngs.remove(marker.getPosition());
        for (int i = 0; i < polylines.size(); i++) {
            polylines.get(i).remove();
        }*/
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
                    return "Title";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Title";
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
                    return "Snippet";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Snippet";
    }

    private void addMarker(LatLng latLng) {

        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_marker, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
        numTxt.setText(title[latLngs.size()]);

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getTitle(latLng))
                .snippet(getSnippet(latLng))
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker))));
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
        polylines.add(polyline);
        polyline.setClickable(true);
    }

    private void drawPolygon() {
        if (latLngs.size() == 4) {
            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .add(latLngs.get(0), latLngs.get(1), latLngs.get(2), latLngs.get(3))
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(getResources().getColor(R.color.transparent_green)));
            polygon.setClickable(true);
        }
    }

    private double getDistance(LatLng latLng1, LatLng latLng2) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(latLng1.latitude);
        startPoint.setLongitude(latLng1.longitude);

        Location endPoint = new Location("locationB");
        endPoint.setLatitude(latLng2.latitude);
        endPoint.setLongitude(latLng2.longitude);

        return startPoint.distanceTo(endPoint);
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}