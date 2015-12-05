package com.ujhrkzy.accelerometersender;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.opengl.Matrix;
import android.util.Log;

public class Estimater1 {

    private final static float PI = (float) Math.PI;
    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;

    private final float[] outputRotationMatrix = new float[16];
    private float[] rotationMatrix = new float[16];

    private float[] rotationMatrix_d = new float[16];

    // configurations
    private boolean landscape = false; // swapXY

    private long lastGyroTime = 0;
    private long lastAccelTime = -1;

    private final Vector3f accVec = new Vector3f();
    private final Vector3f accVecN = new Vector3f(0, 0, 0);
    private final Vector3f vVec = new Vector3f(0, 0, 0);
    private final Vector3f posVec = new Vector3f(0, 0, 0);
    private final Vector3f gyroVec = new Vector3f();
    public float posIntegretedError = 0;

    private float[] outputPosition = new float[3];
    private float[] position = new float[3]; // beta

    private float[] pos = null;

    public Estimater1() {
        reset();
    }

    public void reset() {
        Log.d("OrientationEstimater", "reset");
        posIntegretedError = 0;
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setIdentityM(rotationMatrix_d, 0);
        Matrix.setIdentityM(outputRotationMatrix, 0);
        position[0] = 0;
        position[1] = 0;
        position[2] = 0;
        posVec.set(0, 0, 0);
        vVec.set(0, 0, 0);
        lastAccelTime = -1;
        pos = create();
        lastGyroTime = System.currentTimeMillis();
    }

    /**
     * @return float array [x,y,z] unit:mm
     */
    public float[] getPosition() {
        // outputPosition[0] = position[0] + posVec.values[0];
        // outputPosition[1] = position[1] + posVec.values[1];
        // outputPosition[2] = position[2] + posVec.values[2];
        // outputPosition[0] = posVec.values[0];
        // outputPosition[1] = posVec.values[1];
        // outputPosition[2] = posVec.values[2];
        return pos == null ? create() : pos;
        // return outputPosition;
    }

    public void onSensorEvent(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (landscape) {
                accVecN.set(-event.values[1], event.values[0], -event.values[2]);
            } else {
                accVecN.set(event.values[0], event.values[1], event.values[2]);
            }
            if (lastAccelTime == -1) {
                lastAccelTime = event.timestamp;
                return;
            }
            // if (true) {
            // pos = create();
            // pos[0] = event.values[0];
            // pos[1] = event.values[1];
            // pos[2] = event.values[2];
            // return;
            // }

            float dt = (event.timestamp - lastAccelTime) * NS2S; // dt(sec)
            // System.out.println("dt:" + dt);
            // System.out.println("x1:" + event.values[0]);
            float num = 1000;

            // posVec.values[0] += (event.values[0] * dt * dt * num);
            // posVec.values[1] += (event.values[1] * dt * dt * num);
            // posVec.values[2] += (event.values[2] * dt * dt * num);
            //
            // float[] old = pos == null ? create() : pos;
            // pos = new float[3];
            // pos[0] = posVec.values[0];
            // pos[1] = (event.values[1] * dt * dt * num);
            // pos[2] = (event.values[2] * dt * dt * num);
            // System.out.println("newx:" + pos[0]);
            // BigDecimal newx = new BigDecimal(pos[0]);
            // BigDecimal oldx = new BigDecimal(old[0]);
            // BigDecimal sum = newx.add(oldx);
            // System.out.println(sum);
            // pos[0] = pos[0] + old[0];
            // System.out.println("oldx:" + old[0]);
            // System.out.println("resultx:" + pos[0]);
            // pos[1] = pos[1] + old[1];

            // m/s^2
            // Matrix.multiplyMV(accVec.values, 0, rotationMatrix, 0,
            // accVecN.values, 0); // rotMatrix * groundA

            // velocity(mm/s)
            // vVec.values[0] += accVec.values[0] * dt * 1000;
            // vVec.values[1] += accVec.values[1] * dt * 1000;
            // vVec.values[2] += accVec.values[2] * dt * 1000;
            float[] oldv = new float[3];
            oldv[0] = vVec.values[0];
            oldv[1] = vVec.values[1];
            oldv[2] = vVec.values[2];
            vVec.values[0] = accVecN.values[0] * dt * num / 2;
            vVec.values[1] += accVecN.values[1] * dt * num;
            vVec.values[2] += accVecN.values[2] * dt * num;

            System.out.println("ax:" + event.values[0]);
            // position(mm)
            if (Math.abs(vVec.values[0]) > 8f && Math.abs(vVec.values[0]) < 40f) {
                System.out.println("include:" + vVec.values[0]);
                posVec.values[0] = vVec.values[0] * dt + oldv[0] * dt;
                posVec.values[1] = vVec.values[1] * dt;
                posVec.values[2] = vVec.values[2] * dt;
            } else {
                if (Math.abs(vVec.values[0]) > 16f) {
                    System.out.println(vVec.values[0]);
                } else {
                    System.err.println(vVec.values[0]);
                }
                lastAccelTime = event.timestamp;
                vVec.values[0] = oldv[0];
                vVec.values[1] = oldv[1];
                vVec.values[2] = oldv[2];
                vVec.set(0, 0, 0);
                return;
            }
            // pos = create();
            pos = pos == null ? create() : pos;
            pos[0] += posVec.values[0];
            pos[1] += posVec.values[1];
            pos[2] += posVec.values[2];

            // pos[0] = event.values[0];
            // pos[1] = event.values[1];
            // pos[2] = event.values[2];
            // accVec.set(accVecN.values);
            // accVec.normalize();
            lastAccelTime = event.timestamp;
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (lastGyroTime > 0) {
                float dt = (event.timestamp - lastGyroTime) * NS2S;
                if (landscape) {
                    gyroVec.set(event.values[1], -event.values[0],
                            event.values[2]);
                } else {
                    gyroVec.set(event.values[0], event.values[1],
                            event.values[2]);
                }
                Matrix.rotateM(rotationMatrix, 0, gyroVec.length() * dt * 180
                        / PI, gyroVec.array()[0], gyroVec.array()[1],
                        gyroVec.array()[2]);
            }
            lastGyroTime = event.timestamp;
        }
    }

    private float[] create() {
        float[] val = new float[3];
        val[0] = 0;
        val[1] = 0;
        val[2] = 0;
        return val;
    }
}
