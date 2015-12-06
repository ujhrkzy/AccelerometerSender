package com.ujhrkzy.accelerometersender;

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
