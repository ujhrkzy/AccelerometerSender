package com.ujhrkzy.positionrecognition.bluetooth;

import com.ujhrkzy.positionrecognition.AccelerometerEventListener;
import com.ujhrkzy.positionrecognition.linearaccelerometer.PositionValue;

/**
 * {@link BluetoothAccelerometerEventListener}
 * 
 * @author ujhrkzy
 *
 */
public class BluetoothAccelerometerEventListener implements
        AccelerometerEventListener {
    private BluetoothConnector connector;

    public BluetoothAccelerometerEventListener(BluetoothConnector connector) {
        this.connector = connector;
    }

    @Override
    public void accept(PositionValue value) {
        String msg;
        if (value == null) {
            msg = String.format("x:%s,y:%s,z:%s", 0, 0, 0);
        } else {
            msg = String.format("x:%s,y:%s,z:%s", value.getValueX(),
                    value.getValueY(), value.getValueZ());
        }
        connector.doSend(msg);
    }
}