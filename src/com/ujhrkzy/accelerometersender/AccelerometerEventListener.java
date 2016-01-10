package com.ujhrkzy.accelerometersender;

import com.ujhrkzy.accelerometersender.linearaccelerometer.PositionValue;

/**
 * {@link AccelerometerEventListener}
 * 
 * @author ujhrkzy
 *
 */
public interface AccelerometerEventListener {

    /**
     * {@link PositionValue} を処理します。
     * 
     * @param value
     *            {@link PositionValue}
     */
    void accept(PositionValue value);
}
