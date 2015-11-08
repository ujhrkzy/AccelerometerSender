package com.ujhrkzy.accelerometersender;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerSensor implements SensorEventListener {

    private SensorManager sensorManager;

    private List<AccelerometerEventListener> listeners;

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
        List<Sensor> sensorList = sensorManager
                .getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList != null && !sensorList.isEmpty()) {
            Sensor sensor = sensorList.get(0);
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
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
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        AccelerometerValue value = new AccelerometerValue(event.values[0],
                event.values[1], event.values[2]);
        for (AccelerometerEventListener listener : listeners) {
            listener.accept(value);
        }
    }
}