package com.ujhrkzy.accelerometersender.bluetooth;

import com.ujhrkzy.accelerometersender.AccelerometerEventListener;
import com.ujhrkzy.accelerometersender.linearaccelerometer.PositionValue;

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