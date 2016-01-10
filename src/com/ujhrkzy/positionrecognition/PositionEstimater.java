package com.ujhrkzy.positionrecognition;

import com.ujhrkzy.positionrecognition.linearaccelerometer.PositionValue;

/**
 * {@link PositionEstimater}
 * 
 * @author ujhrkzy
 *
 */
public interface PositionEstimater {

    /**
     * {@link PositionValue} を処理します。
     * 
     * @param value
     *            {@link PositionValue}
     */
    void accept(PositionValue value);
}
