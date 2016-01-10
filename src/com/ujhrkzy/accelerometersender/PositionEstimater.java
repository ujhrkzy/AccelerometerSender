package com.ujhrkzy.accelerometersender;

import com.ujhrkzy.accelerometersender.linearaccelerometer.PositionValue;

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
