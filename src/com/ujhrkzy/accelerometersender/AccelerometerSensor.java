package com.ujhrkzy.accelerometersender;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerSensor implements SensorEventListener {

    // private final OrientationEstimater estimater = new
    // OrientationEstimater();
    private final Estimater1 estimater = new Estimater1();

    private final List<AccelerometerEventListener> listeners;
    private SensorManager sensorManager;

    public AccelerometerSensor(List<AccelerometerEventListener> listeners) {
        this.sensorManager = null;
        this.listeners = listeners;
    }

    public void onCreate(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        onResume();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * アクティビティが動き始めたらリスナーを登録する
     */
    public void onResume() {
        if (sensorManager == null) {
            return;
        }
        // Sensor sensorAccel = sensorManager
        // .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensorAccel = sensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensorAccel,
                SensorManager.SENSOR_DELAY_UI);

        // Sensor sensorGyro = sensorManager
        // .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // sensorManager.registerListener(this, sensorGyro,
        // SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * アクティビティがポーズになったらリスナーを止める
     */
    public void onPause() {
        if (sensorManager == null) {
            return;
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        // if (type != Sensor.TYPE_ACCELEROMETER && type !=
        // Sensor.TYPE_GYROSCOPE) {
        // return;
        // }
        if (type != Sensor.TYPE_LINEAR_ACCELERATION
                && type != Sensor.TYPE_GYROSCOPE) {
            return;
        }

        estimater.onSensorEvent(event);
        float[] position = estimater.getPosition();
        AccelerometerValue value = new AccelerometerValue(position[0],
                position[1], position[2]);
        for (AccelerometerEventListener listener : listeners) {
            listener.accept(value);
        }
    }

    public void reset() {
        estimater.reset();
    }
}