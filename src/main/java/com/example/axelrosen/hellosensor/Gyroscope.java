package com.example.axelrosen.hellosensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class Gyroscope extends AppCompatActivity implements SensorEventListener
{
    private TextView gyroText;
    private SensorManager sManager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        gyroText = (TextView) findViewById(R.id.textview);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onStop()
    {
        sManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }

        gyroText.setText("X value: "+ Float.toString(event.values[2]) +"\n"+
                "Y value: "+ Float.toString(event.values[1]) +"\n"+
                "Z value: "+ Float.toString(event.values[0]));
    }
}
