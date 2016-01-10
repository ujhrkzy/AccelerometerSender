package com.ujhrkzy.positionrecognition;

import com.ujhrkzy.positionrecognition.linearaccelerometer.PositionValue;

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
