package com.example.andriod.accelerometer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andriod.accelerometer.R;

import static android.R.attr.angle;

public class MainActivity extends Activity implements SensorEventListener {
    private TextView text_x;
    private TextView text_y;
    private TextView text_z;
    private SensorManager aSensorManager;
    private Sensor aSensor;
    private float gravity[] = new float[3];

    private TextView text_aax;
    private TextView text_aay;
    private TextView text_aaz;
    private Sensor aaSensor;
    private float angularAcceleration[] = new float[3];

    private TextView text_anglex;
    private TextView text_angley;
    private TextView text_anglez;
    private Sensor angleSensor;
    private float angle[] = new float[3];

    private TextView text_time;
    private float timestamp=0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        text_x = (TextView)findViewById(R.id.TextView01);
        text_y = (TextView)findViewById(R.id.TextView02);
        text_z = (TextView)findViewById(R.id.TextView03);

        text_aax = (TextView)findViewById(R.id.TextView11);
        text_aay = (TextView)findViewById(R.id.TextView12);
        text_aaz = (TextView)findViewById(R.id.TextView13);

        text_anglex = (TextView)findViewById(R.id.TextView21);
        text_angley = (TextView)findViewById(R.id.TextView22);
        text_anglez = (TextView)findViewById(R.id.TextView23);

        text_time = (TextView)findViewById(R.id.TextViewt);

        aSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(aSensor==null)
        {
            text_aay.setText("No acc");
        }
        aSensorManager.registerListener(this, aSensor, aSensorManager.SENSOR_DELAY_NORMAL);

        aSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        aaSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(aaSensor==null){

            text_aax.setText("No gyro");

        }
        aSensorManager.registerListener(this,aaSensor,aSensorManager.SENSOR_DELAY_NORMAL);

        aSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        angleSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if(angleSensor==null)
        {
            text_anglex.setText("No rotation vector");
        }
        aSensorManager.registerListener(this,angleSensor,aSensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// TODO Auto-generated method stub

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
// TODO Auto-generated method stub

        if(timestamp==0)
        {
            timestamp = event.timestamp;
        }
        else
        {
            text_time.setText("Time: " + (event.timestamp - timestamp) );
        }


        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = event.values[0];
            gravity[1] = event.values[1];
            gravity[2] = event.values[2];
            text_x.setText("X = " + gravity[0]);
            text_y.setText("Y = " + gravity[1]);
            text_z.setText("Z = " + gravity[2]);
        }
        else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE)
        {
            angularAcceleration[0] = event.values[0];
            angularAcceleration[1] = event.values[1];
            angularAcceleration[2] = event.values[2];
            text_aax.setText("X = " + angularAcceleration[0]);
            text_aay.setText("Y = " + angularAcceleration[1]);
            text_aaz.setText("Z = " + angularAcceleration[2]);
        }
        else if(event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR)
        {
            angle[0] = event.values[0];
            angle[1] = event.values[1];
            angle[2] = event.values[2];
            text_anglex.setText("X = " + angle[0]);
            text_angley.setText("Y = " + angle[1]);
            text_anglez.setText("Z = " + angle[2]);
        }
    }
    @Override
    protected void onPause()
    {
// TODO Auto-generated method stub
/* Unregister SensorEventListener */
        aSensorManager.unregisterListener(this);
        Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
        super.onPause();
    }
}