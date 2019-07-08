package com.example.moveroad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor walkingSensor;
    GpsInfo gps;
    Context context;
    Activity activity;
    TextView state;
    TextView walkingView;
    TextView bike;
    TextView car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //풀스크린
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //액티비티를 최상위로
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        context = this;
        activity = this;
        setValue();
        gps = new GpsInfo(context, activity);
    }

    private void setValue() {
        state = (TextView) findViewById(R.id.State);
        walkingView = (TextView) findViewById(R.id.Walking);
        bike = (TextView) findViewById(R.id.Bike);
        car = (TextView) findViewById(R.id.Car);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        walkingSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(walkingSensor == null) {
            Toast.makeText(this, "지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setState() {
        if(gps.stateNo == 3) state.setText("현재 상태 : 자동차");
        else if (gps.stateNo == 2) state.setText("현재 상태 : 자전거");
        else if(gps.stateNo == 0) state.setText("현재 상태 : 없음");
    }

    public void setGPS() {
        if(gps.isGetLocation()) {
            //double latitude = gps.getLatitude();
            //double longitude = gps.getLongitude();
            Location distance = gps.getLocation();
            if(gps.speed > 20) {
                car.setText("자동차 : " + distance + " km");
            }
            else if(gps.speed >5 && gps.speed <= 20) {
                bike.setText("자전거 : " + distance + " km");
            }
        }
        else gps.showSettingAlert();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGPS();
        setState();
        sensorManager.registerListener(this, walkingSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER && gps.stateNo == 1) {
            state.setText("현재 상태 : 걷기");
            walkingView.setText("걷기 : " + (int) sensorEvent.values[0] + " 걸음");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
