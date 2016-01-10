package com.ujhrkzy.positionrecognition.linearaccelerometer;

import android.hardware.SensorEvent;

/**
 * {@link LinearAccelerometerPositionCalibrator}
 * 
 * @author ujhrkzy
 *
 */
public class LinearAccelerometerPositionCalibrator {
    private static final int COUNT_MAX = 100;
    private float[] positions = { 0, 0, 0 };
    private float[] averagePositions = null;
    private int calibrateCount = 0;

    /**
     * calibrate します。
     * 
     * @param event
     *            {@link SensorEvent}
     * @return calibrate が完了した場合、 {@code true}
     */
    public boolean calibrate(SensorEvent event) {
        if (isFinished()) {
            return true;
        }
        sum(event);
        calibrateCount++;
        if (!isFinished()) {
            return false;
        }
        if (averagePositions == null) {
            calculateAveragePositions();
        }
        return true;
    }

    /**
     * Calibration した値をリセットします。
     */
    public void reset() {
        calibrateCount = 0;
        positions = new float[3];
        positions[0] = 0;
        positions[1] = 0;
        positions[2] = 0;
        averagePositions = null;
    }

    private void sum(SensorEvent event) {
        positions[0] += event.values[0];
        positions[1] += event.values[1];
        positions[2] += event.values[2];
    }

    private void calculateAveragePositions() {
        averagePositions = new float[3];
        averagePositions[0] = positions[0] / (float) COUNT_MAX;
        averagePositions[1] = positions[1] / (float) COUNT_MAX;
        averagePositions[2] = positions[2] / (float) COUNT_MAX;
    }

    private boolean isFinished() {
        return calibrateCount >= COUNT_MAX;
    }

    public float[] getPositions() {
        return averagePositions;
    }
}
