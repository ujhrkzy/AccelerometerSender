package com.ujhrkzy.positionrecognition;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.ujhrkzy.positionrecognition.gyroscope.GyroscopePositionEstimator;
import com.ujhrkzy.positionrecognition.linearaccelerometer.PositionValue;

/**
 * 
 * {@link AccelerometerSensor}
 *
 * @author ujhrkzy
 *
 */
public class AccelerometerSensor implements SensorEventListener {

    // TBD {@link PositionEstimater} で抽象化する。
    private final GyroscopePositionEstimator estimater = new GyroscopePositionEstimator();
    private final List<AccelerometerEventListener> listeners;
    private final SensorManager sensorManager;

    /**
     * Constructor
     * 
     * @param listeners
     *            {@link AccelerometerEventListener} のリスト
     */
    public AccelerometerSensor(SensorManager sensorManager,
            List<AccelerometerEventListener> listeners) {
        // TBD null check
        this.sensorManager = sensorManager;
        this.listeners = listeners;
        onResume();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * アクティビティが動き始めたらリスナーを登録する
     */
    public void onResume() {
        // Sensor sensorAccel = sensorManager
        // .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        // sensorManager.registerListener(this, sensorAccel,
        // SensorManager.SENSOR_DELAY_UI);
        Sensor sensorGyroscope = sensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensorGyroscope,
                SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * アクティビティがポーズになったらリスナーを止める
     */
    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type != Sensor.TYPE_GYROSCOPE) {
            return;
        }
        estimater.onSensorEvent(event);
        float[] position = estimater.getPosition();
        PositionValue value = position == null ? null : new PositionValue(
                position[0], position[1], position[2]);
        for (AccelerometerEventListener listener : listeners) {
            listener.accept(value);
        }
    }

    /**
     * 値をリセットします。
     */
    public void reset() {
        estimater.reset();
    }
}