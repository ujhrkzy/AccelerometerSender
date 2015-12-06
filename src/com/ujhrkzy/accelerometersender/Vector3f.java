package com.ujhrkzy.accelerometersender;

/**
 * {@link Vector3f}
 * 
 * @author ujhrkzy
 *
 */
public class Vector3f {

    private final float values[] = new float[4];

    public Vector3f(float x, float y, float z) {
        values[0] = x;
        values[1] = y;
        values[2] = z;
        values[3] = 1;
    }

    public Vector3f() {
        this(0, 0, 0);
    }

    public Vector3f(float[] a) {
        this(a[0], a[1], a[2]);
    }

    public Vector3f(Vector3f v) {
        this(v.getValues());
    }

    public float[] getValues() {
        return values;
    }

    public void set(float x, float y, float z) {
        values[0] = x;
        values[1] = y;
        values[2] = z;
        values[3] = 1;
    }

    public void set(float[] a) {
        set(a[0], a[1], a[2]);
    }

    public float length() {
        return (float) Math.sqrt(length2());
    }

    public float length2() {
        return values[0] * values[0] + values[1] * values[1] + values[2]
                * values[2];
    }

    public void scale(float s) {
        values[0] *= s;
        values[1] *= s;
        values[2] *= s;
    }

    public String toString() {
        return "( " + values[0] + ", " + values[1] + ", " + values[2] + " )";
    }

    public static Vector3f add(Vector3f result, Vector3f a, Vector3f b) {
        result.values[0] = a.values[0] + b.values[0];
        result.values[1] = a.values[1] + b.values[1];
        result.values[2] = a.values[2] + b.values[2];
        return result;
    }

    public Vector3f add(Vector3f b) {
        return add(this, this, b);
    }

    public static Vector3f sub(Vector3f result, Vector3f a, Vector3f b) {
        result.values[0] = a.values[0] - b.values[0];
        result.values[1] = a.values[1] - b.values[1];
        result.values[2] = a.values[2] - b.values[2];
        return result;
    }

    public Vector3f sub(Vector3f b) {
        return sub(this, this, b);
    }
}
