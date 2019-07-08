package com.example.moveroad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

public class GpsInfo extends Service implements LocationListener {
    Context ctx;
    Activity atv;

    public GpsInfo(Context context, Activity activity) {
        ctx = context;
        atv = activity;
        getLocation();
    }


    // GPS 사용여부
    boolean isGPSEnabled = false;
    // 네트워크 사용여부
    boolean isNetWorkEnabled = false;
    // GPS 상태값
    boolean isGetLocation = false;
    Location location;
    double lat; // 위도
    double lon; // 경도
    double speed;
    int stateNo = 0;

    private long startTime = -1;

    private Location beforeLocation;

    private Location curLocation;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 1;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;

    public Location getLocation() {
        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);     // 정확도
            criteria.setPowerRequirement(Criteria.POWER_LOW); // 전원소비량
            criteria.setAltitudeRequired(true);              // 고도
            criteria.setBearingRequired(false);              // 기본 정보, 방향, 방위
            criteria.setSpeedRequired(false);                // 속도
            criteria.setCostAllowed(true);                   // 위치정보 비용
            locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(criteria, true);
            // GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 현재 네트워크 상태 값 알아오기
            isNetWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetWorkEnabled) {
            } else {
                if (isNetWorkEnabled) {
                    locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lat = location.getLongitude();
                        }
                    }
                }
                this.isGetLocation = true;
                if (isGPSEnabled) {
                    if (location == null) {                  // LocationManager.GPS_PROVIDER
                        locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    // GPS OFF
    /*
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GpsInfo.this);
        }
    }*/

    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    public void showSettingAlert() {
        if (isGPSEnabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
            alertDialog.setTitle("GPS 사용");
            alertDialog.setMessage("GPS 사용해야합니다. 설정창으로 이동하시겠습니까?");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    ctx.startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (startTime == -1) {
            startTime = location.getTime();
        }

        beforeLocation = getLocation();
        float distance[] = new float[1];
        Location.distanceBetween(beforeLocation.getLatitude(), beforeLocation.getLongitude(), location.getLatitude(), location.getLongitude(), distance); // distance -> meter m/s
        float dis = distance[0];
        location.getSpeed();
        long delay = location.getTime() - startTime;
        speed = distance[0] / delay;
        double speedKMH = speed * 3600;  // m/s
        beforeLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(speed > 20) stateNo = 3;
        else if(speed > 5 && speed <= 20) stateNo = 2;
        else if(speed > 0 && speed <= 5) stateNo = 1;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}