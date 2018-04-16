package com.example.axelrosen.hellosensor;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener {

    ImageView compass_img;
    TextView txt_compass;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        compass_img = (ImageView) findViewById(R.id.compass);
        txt_compass = (TextView) findViewById(R.id.txt_azimuth);

        start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        compass_img.setRotation(-mAzimuth);

        String where = "NW";

        if (mAzimuth >= 350 || mAzimuth <= 10)
            where = "N";
        if (mAzimuth < 350 && mAzimuth > 280)
            where = "NW";
        if (mAzimuth <= 280 && mAzimuth > 260)
            where = "W";
        if (mAzimuth <= 260 && mAzimuth > 190)
            where = "SW";
        if (mAzimuth <= 190 && mAzimuth > 170)
            where = "S";
        if (mAzimuth <= 170 && mAzimuth > 100)
            where = "SE";
        if (mAzimuth <= 100 && mAzimuth > 80)
            where = "E";
        if (mAzimuth <= 80 && mAzimuth > 10)
            where = "NE";


        txt_compass.setText(mAzimuth + "° " + where);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void stop() {
        if(haveSensor && haveSensor2){
            mSensorManager.unregisterListener(this,mAccelerometer);
            mSensorManager.unregisterListener(this,mMagnetometer);
        }
        else{
            if(haveSensor)
                mSensorManager.unregisterListener(this,mRotationV);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }
  // private ImageView imageView;
  // private float[] mGravity = new float[3];
  // private float[] mGeoMagnetic = new float[3];
  // private float azimuth = 0f;
  // private float correctAzimuth = 0f;
  // private SensorManager mSensorManager;
  // private TextView compassText;

  // @Override
  // protected void onCreate(Bundle savedInstanceState) {
  //     super.onCreate(savedInstanceState);
  //     setContentView(R.layout.activity_compass);

  //     imageView = (ImageView)findViewById(R.id.compass);
  //     compassText = (TextView)findViewById(R.id.compassText);
  //     mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);


  // }


  // @Override
  // protected void onPause(){
  //     super.onPause();
  //     mSensorManager.unregisterListener(this);
  // }

  // @Override
  // protected void onResume()
  // {
  //     super.onResume();
  //     /*register the sensor listener to listen to the gyroscope sensor, use the
  //     callbacks defined in this class, and gather the sensor information as quick
  //     as possible*/
  //     mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME);
  //     mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);

  // }

  // @Override
  // public void onSensorChanged(SensorEvent sensorEvent) {
  //     final float alpha = 0.97f;
  //     synchronized (this){
  //         if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
  //             mGravity[0] = alpha*mGravity[0] + (1-alpha)*sensorEvent.values[0];
  //             mGravity[1] = alpha*mGravity[1] + (1-alpha)*sensorEvent.values[1];
  //             mGravity[2] = alpha*mGravity[2] + (1-alpha)*sensorEvent.values[2];
  //         }

  //         if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
  //             mGeoMagnetic[0] = alpha*mGeoMagnetic[0] + (1-alpha)*sensorEvent.values[0];
  //             mGeoMagnetic[1] = alpha*mGeoMagnetic[1] + (1-alpha)*sensorEvent.values[1];
  //             mGeoMagnetic[2] = alpha*mGeoMagnetic[2] + (1-alpha)*sensorEvent.values[2];
  //         }

  //         float R[] = new float[9];
  //         float I[] = new float[9];
  //         boolean success = SensorManager.getRotationMatrix(R,I,mGravity, mGeoMagnetic);
  //         if(success){
  //             float orientation[] = new float[3];
  //             SensorManager.getOrientation(R, orientation);
  //             azimuth = (float)Math.toDegrees(orientation[0]);
  //             azimuth = (azimuth+360)%360;

  //             Animation animation = new RotateAnimation(-correctAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
  //             correctAzimuth = azimuth;
  //             animation.setDuration(500);
  //             animation.setRepeatCount(0);
  //             animation.setFillAfter(true);

  //             imageView.startAnimation(animation);
  //             compassText.setText("Heading: " + mGeoMagnetic[1]);

  //         }
  //     }
  // }

  // @Override
  // public void onAccuracyChanged(Sensor sensor, int i) {

  // }
}
