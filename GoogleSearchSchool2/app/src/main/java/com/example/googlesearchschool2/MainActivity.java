package com.example.googlesearchschool2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.googlesearchschool2.db.AppDatabase_Impl;
import com.example.googlesearchschool2.db.User;
import com.facebook.stetho.Stetho;
import com.example.googlesearchschool2.db.AppDatabase;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.googlesearchschool2.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private List<User> _user;

    private MapView mMapView;
    private ActivityMainBinding binding;

    private ArrayList<School> mSchoolsData;

    private List<User> userList;

    private Double x = 0.0;
    private Double y = 0.0;
    private int enlarge = 13;

    private LocationManager locationManager;
    private String commadStr;

    private int widthPixels;
    private int heightPixels;

    private int _radius = 3100;
    private SeekBar mySeekBar;

    private GoogleMap mMap;
    private MarkerOptions[] _marks;
    private Marker[] _mark;
    private boolean[] showMark;

    private FloatingActionButton fab;

    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_ACCESS_COARSE_LOCATION);

        commadStr = LocationManager.GPS_PROVIDER;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(commadStr, 1000, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(commadStr);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddNewUserActivity.class);
                startActivity(i);
            }
        });

        loadUserList();
        _marks = new MarkerOptions[userList.size()];
        _mark = new Marker[userList.size()];
        showMark = new boolean[userList.size()];

        //MarkerOption
        for(int i = 0; i < userList.size(); i++){
            _marks[i] = new MarkerOptions().position(new LatLng(userList.get(i).schoolLatitude, userList.get(i).schoolLongitude)).title(userList.get(i).schoolName);
        }
/*
        _marks[0] = new MarkerOptions().position(new LatLng(25.0320159, 121.543234)).title("大安高工");
        _marks[1] = new MarkerOptions().position(new LatLng(25.020659, 121.322642)).title("師範大學");
        _marks[2] = new MarkerOptions().position(new LatLng(25.030390, 121.512795)).title("建國中學");
        _marks[3] = new MarkerOptions().position(new LatLng(25.067471, 121.521561)).title("大同大學");
        _marks[4] = new MarkerOptions().position(new LatLng(25.061757, 121.543792)).title("中山國中");
        _marks[5] = new MarkerOptions().position(new LatLng(25.043756, 121.537740)).title("台北科技大學");
*/

        mySeekBar = (SeekBar) findViewById(R.id.seekBar);
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _radius = progress*60 + 100;
                mMap.clear();

                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(x, y))
                        .fillColor(1090453504)
                        .strokeWidth(1)
                        .radius(_radius); // In meters

                // Get back the mutable Circle
                mMap.addMarker(new MarkerOptions().position(new LatLng(x, y)).title("目前位置"));

                mMap.addCircle(circleOptions);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        DisplayMetrics dm;
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthPixels  = dm.widthPixels;
        heightPixels = dm.heightPixels;

        mMapView = (MapView) findViewById(R.id.map);

        mMapView.getLayoutParams().height=heightPixels*2/3;
        mMapView.getLayoutParams().width=widthPixels;

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this);

        mSchoolsData = new ArrayList<>();
        initializeData();

    }

    public void onClick(View view){
        loadUserList();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(x, y)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(enlarge));
        for(int i = 0; i < userList.size(); i++){
            double _distance = getDistance(_marks[i].getPosition().longitude, _marks[i].getPosition().latitude, y, x)*1000;
            Log.i("distance", Double.toString(_distance));
            if(_distance < _radius){
                _mark[i] = mMap.addMarker(_marks[i]);
                showMark[i] = true;
            }
            else{
                showMark[i] = false;
            }
        }
    }

    private void loadUserList() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        userList =db.userDao().getAllUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            x = location.getLatitude();
            //Log.i("g1", Double.toString(x));
            y = location.getLongitude();
            //Log.i("g2", Double.toString(y));

        }
        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        LatLng sydney = new LatLng(x, y);
        mMap.addMarker(new MarkerOptions().position(sydney).title("目前位置"));

        //mMap.setMyLocationEnabled(true); // 右上角的定位功能；這行會出現紅色底線，不過仍可正常編譯執行

        CircleOptions circleOptions = new CircleOptions()
                .center(sydney)
                .fillColor(1090453504)
                .strokeWidth(1)
                .radius(_radius); // In meters

        // Get back the mutable Circle
        mMap.addCircle(circleOptions);

        mMap.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
        mMap.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(enlarge));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                TypedArray schoolsImageResources = getResources()
                        .obtainTypedArray(R.array.schools_images);

                for(int j = 0; j < userList.size(); j++){
                    //Log.i("Mark_num", Integer.toString(_mar.size()));
                    if(showMark[j]){
                        if(marker.equals(_mark[j])){

                            Intent i = new Intent(MainActivity.this, DetailActivity.class);
                            i.putExtra("title", userList.get(j).schoolName);
                            i.putExtra("Info", userList.get(j).schoolInfo);
                            i.putExtra("Image", schoolsImageResources.getResourceId(userList.get(j).ColorImage, 0));
                            startActivity(i);

                        }
                    }

                }
                schoolsImageResources.recycle();

                return false;
            }
        });


    }
    private void initializeData() {
        // Get the resources from the XML file.
        String[] schoolsList = getResources()
                .getStringArray(R.array.school_titles);
        String[] schoolsInfo = getResources()
                .getStringArray(R.array.subtitle_detail_text);


        // Clear the existing data (to avoid duplication).
        mSchoolsData.clear();

        // Create the ArrayList of Sports objects with the titles and
        // information about each sport

        // Recycle the typed array.

    }

    public static double getDistance(double LonA, double LatA, double LonB, double LatB)
    {
        // 東西經,南北緯處理,只在國內可以不處理(假設都是北半球,南半球只有澳洲具有應用意義)
        double MLonA = LonA;
        double MLatA = LatA;
        double MLonB = LonB;
        double MLatB = LatB;
        // 地球半徑(千米)
        double R = 6371.004;
        double C = Math.sin(rad(LatA)) * Math.sin(rad(LatB)) + Math.cos(rad(LatA)) * Math.cos(rad(LatB)) * Math.cos(rad(MLonA - MLonB));
        return (R * Math.acos(C));
    }
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
}