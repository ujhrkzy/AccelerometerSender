package com.ujhrkzy.accelerometersender;

public class AccelerometerValue {

    private final float valueX;
    private final float valueY;
    private final float valueZ;

    public AccelerometerValue(float valueX, float valueY, float valueZ) {
        this.valueX = valueX;
        this.valueY = valueY;
        this.valueZ = valueZ;
    }

    public float getValueX() {
        return valueX;
    }

    public float getValueY() {
        return valueY;
    }

    public float getValueZ() {
        return valueZ;
    }

}
