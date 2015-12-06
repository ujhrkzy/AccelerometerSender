package com.ujhrkzy.accelerometersender;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * {@link PositionEstimator}
 * 
 * @author ujhrkzy
 *
 */
public class PositionEstimator {

    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    // Create a constant to convert Meter to MilliMeter
    private static final float M2MM = 1000f;
    private final PositionCalibrator calibrator = new PositionCalibrator();
    private final boolean landscape;
    private final Vector3f accVector = new Vector3f(0, 0, 0);
    private final Vector3f velocityVector = new Vector3f(0, 0, 0);
    private final Vector3f posVec = new Vector3f(0, 0, 0);
    private long[] lastAccelTime = create(-1l);
    private float[] pos = null;

    /**
     * Constructor
     */
    public PositionEstimator() {
        this(false);
    }

    /**
     * Constructor
     * 
     * @param landscape
     *            landscapeの場合、 {@code true}
     */
    public PositionEstimator(boolean landscape) {
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
        System.out.println("index0:" + index0Value);
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
        velocityVector.getValues()[index] = accVector.getValues()[index] * dt
                * M2MM;
        // velocityVector.values[0] += accVector.values[0] * dt * M2MM;

        if (checkVelocity(velocityVector.getValues()[index])) {
            // x(t) = 1/2 * 加速度 * t^2 + v0 * t + x0
            posVec.getValues()[index] = velocityVector.getValues()[index] * dt
                    / 2 + (oldVelocity * dt);
            pos[index] += posVec.getValues()[index];
        } else {
            velocityVector.getValues()[index] = 0;
        }
        lastAccelTime[index] = currentEventTime;
    }

    private boolean checkVelocity(float velocity) {
        float absVelocity = Math.abs(velocity);
        return absVelocity > 16f && absVelocity < 80f;
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
