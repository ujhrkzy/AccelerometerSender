package com.ujhrkzy.positionrecognition.linearaccelerometer;

/**
 * {@link PositionValue}
 * 
 * @author ujhrkzy
 *
 */
public class PositionValue {

    private final float valueX;
    private final float valueY;
    private final float valueZ;

    /**
     * Constructor
     * 
     * @param valueX
     *            x
     * @param valueY
     *            y
     * @param valueZ
     *            z
     */
    public PositionValue(float valueX, float valueY, float valueZ) {
        this.valueX = valueX;
        this.valueY = valueY;
        this.valueZ = valueZ;
    }

    /**
     * x の値を返却します。
     * 
     * @return x
     */
    public float getValueX() {
        return valueX;
    }

    /**
     * y の値を返却します。
     * 
     * @return y
     */
    public float getValueY() {
        return valueY;
    }

    /**
     * z の値を返却します。
     * 
     * @return z
     */
    public float getValueZ() {
        return valueZ;
    }

}
