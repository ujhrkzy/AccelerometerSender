package com.ujhrkzy.positionrecognition.linearaccelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * {@link LinearAccelerometerPositionEstimator}
 * 
 * @author ujhrkzy
 *
 */
public class LinearAccelerometerPositionEstimator {

    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    // Create a constant to convert Meter to MilliMeter
    private static final float M2MM = 1000.0f;
    private final LinearAccelerometerPositionCalibrator calibrator = new LinearAccelerometerPositionCalibrator();
    private final boolean landscape;
    private final Vector3f accVector = new Vector3f(0, 0, 0);
    private final Vector3f velocityVector = new Vector3f(0, 0, 0);
    private final Vector3f posVec = new Vector3f(0, 0, 0);
    private long[] lastAccelTime = create(-1l);
    private float[] pos = null;

    /**
     * Constructor
     */
    public LinearAccelerometerPositionEstimator() {
        this(false);
    }

    /**
     * Constructor
     * 
     * @param landscape
     *            landscapeの場合、 {@code true}
     */
    public LinearAccelerometerPositionEstimator(boolean landscape) {
        this.landscape = landscape;
    }

    /**
     * ポジション情報をリセットします。
     */
    public void reset() {
        calibrator.reset();
        posVec.set(0, 0, 0);
        velocityVector.set(0, 0, 0);
        lastAccelTime = create(-1l);
        pos = null;
    }

    public void onSensorEvent(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            acceptLinearAccelerationEvent(event);
        }
    }

    /**
     * ポジション情報を返却します。
     * 
     * @return float array [x,y,z] unit:mm
     */
    public float[] getPosition() {
        return pos;
    }

    // private void check(SensorEvent event) {
    // pos = create(0f);
    // pos[0] = event.values[0];
    // pos[1] = event.values[1];
    // pos[2] = event.values[2];
    // }

    private void acceptLinearAccelerationEvent(SensorEvent event) {
        if (!calibrator.calibrate(event)) {
            lastAccelTime[0] = event.timestamp;
            lastAccelTime[1] = event.timestamp;
            lastAccelTime[2] = event.timestamp;
            return;
        }
        pos = pos == null ? create(0f) : pos;
        float[] initialValues = calibrator.getPositions();
        float index0Value = event.values[0] - initialValues[0];
        float index1Value = event.values[1] - initialValues[1];
        float index2Value = event.values[2] - initialValues[2];
        if (landscape) {
            accVector.set(-index1Value, index0Value, -index2Value);
        } else {
            accVector.set(index0Value, index1Value, index2Value);
        }
        long currentEventTime = event.timestamp;
        estimatePosition(0, currentEventTime);
        estimatePosition(1, currentEventTime);
        estimatePosition(2, currentEventTime);
    }

    private void estimatePosition(int index, long currentEventTime) {
        // dt(sec)
        float dt = (currentEventTime - lastAccelTime[index]) * NS2S;

        // velocity(mm/s)
        float oldVelocity = velocityVector.getValues()[index];
        velocityVector.setValue(index, accVector.getValues()[index] * dt * M2MM
                + oldVelocity);
        // velocityVector.values[0] += accVector.values[0] * dt * M2MM;

        if (checkVelocity(velocityVector.getValues()[index])) {
            // x(t) = 1/2 * 加速度 * t^2 + v0 * t + x0
            posVec.setValue(index, (accVector.getValues()[index] * dt * dt
                    * M2MM / 2)
                    + (oldVelocity * dt));
            // posVec.setValue(index, velocityVector.getValues()[index] * dt
            // + (oldVelocity * dt));
            pos[index] += posVec.getValues()[index];
        } else {
            velocityVector.setValue(index, 0);
        }
        lastAccelTime[index] = currentEventTime;
    }

    private boolean checkVelocity(float velocity) {
        float absVelocity = Math.abs(velocity);
        return absVelocity > 80f && absVelocity < 10000f;
    }

    private float[] create(float value) {
        float[] val = new float[3];
        val[0] = value;
        val[1] = value;
        val[2] = value;
        return val;
    }

    private long[] create(long value) {
        long[] val = new long[3];
        val[0] = value;
        val[1] = value;
        val[2] = value;
        return val;
    }
}
