package com.ujhrkzy.positionrecognition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ujhrkzy.accelerometersender.R;
import com.ujhrkzy.positionrecognition.bluetooth.BluetoothAccelerometerEventListener;
import com.ujhrkzy.positionrecognition.bluetooth.BluetoothConnector;
import com.ujhrkzy.positionrecognition.linearaccelerometer.PositionValue;

/**
 * {@link MainActivity}
 * 
 * @author ujhrkzy
 *
 */
public class MainActivity extends Activity {
    private final static int DEVICES_DIALOG = 1;
    private final static int ERROR_DIALOG = 2;
    private final BluetoothConnector bluetoothConnector;
    private AccelerometerSensor accelerometerSensor;
    private ProgressDialog waitDialog;
    private EditText editTextX;
    private EditText editTextY;
    private EditText editTextZ;
    private CheckBox calibration;
    private String errorMessage = "";

    /**
     * Constructor
     */
    public MainActivity() {
        this.bluetoothConnector = new BluetoothConnector(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextX = (EditText) findViewById(R.id.editText1);
        editTextY = (EditText) findViewById(R.id.editText2);
        editTextZ = (EditText) findViewById(R.id.editText3);
        calibration = (CheckBox) findViewById(R.id.calibrationCheckBox);
        List<AccelerometerEventListener> listeners = new ArrayList<AccelerometerEventListener>();
        AccelerometerEventListener listener = createViewEventListener(
                editTextX, editTextY, editTextZ);
        listeners.add(listener);
        listeners.add(new BluetoothAccelerometerEventListener(bluetoothConnector));
        this.accelerometerSensor = new AccelerometerSensor(
                (SensorManager) this.getSystemService(Context.SENSOR_SERVICE),
                listeners);

        Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometerSensor.reset();
                bluetoothConnector.doSend("reset");
            }
        });
    }

    private AccelerometerEventListener createViewEventListener(
            final EditText editTextX, final EditText editTextY,
            final EditText editTextZ) {
        return new AccelerometerEventListener() {

            @Override
            public void accept(PositionValue value) {
                if (value == null) {
                    calibration.setChecked(true);
                    editTextX.setText(null);
                    editTextY.setText(null);
                    editTextZ.setText(null);
                    return;
                }
                calibration.setChecked(false);
                editTextX.setText(String.valueOf(value.getValueX()));
                editTextY.setText(String.valueOf(value.getValueY()));
                editTextZ.setText(String.valueOf(value.getValueZ()));
            }
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        accelerometerSensor.onResume();
        bluetoothConnector.init();
        // ペアリング済みデバイスの一覧を表示してユーザに選ばせる。
        showDialog(DEVICES_DIALOG);
    }

    @Override
    protected void onPause() {
        super.onStop();
        accelerometerSensor.onPause();
    }

    @Override
    protected void onDestroy() {
        bluetoothConnector.doClose();
        super.onDestroy();
    }

    // ----------------------------------------------------------------
    // 以下、ダイアログ関連
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DEVICES_DIALOG)
            return createDevicesDialog();
        if (id == ERROR_DIALOG)
            return createErrorDialog();
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        if (id == ERROR_DIALOG) {
            ((AlertDialog) dialog).setMessage(errorMessage);
        }
        super.onPrepareDialog(id, dialog);
    }

    private Dialog createDevicesDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Select device");

        // ペアリング済みデバイスをダイアログのリストに設定する。
        Set<BluetoothDevice> pairedDevices = bluetoothConnector.getPairedDevices();
        final BluetoothDevice[] devices = pairedDevices
                .toArray(new BluetoothDevice[0]);
        String[] items = new String[devices.length];
        for (int i = 0; i < devices.length; i++) {
            items[i] = devices[i].getName();
        }

        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 選択されたデバイスを通知する。そのまま接続開始。
                        bluetoothConnector.doConnect(devices[which]);
                    }
                });
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder.create();
    }

    /**
     * エラーダイアログを表示します。
     * 
     * @param msg
     *            メッセージ
     */
    @SuppressWarnings("deprecation")
    public void errorDialog(String msg) {
        if (this.isFinishing())
            return;
        this.errorMessage = msg;
        this.showDialog(ERROR_DIALOG);
    }

    private Dialog createErrorDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Error");
        alertDialogBuilder.setMessage("");
        alertDialogBuilder.setPositiveButton("Exit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        return alertDialogBuilder.create();
    }

    /**
     * 待機ダイアログを表示します。
     * 
     * @param msg
     *            メッセージ
     */
    public void showWaitDialog(String msg) {
        if (waitDialog == null) {
            waitDialog = new ProgressDialog(this);
        }
        waitDialog.setMessage(msg);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.show();
    }

    /**
     * 待機ダイアログを隠します。
     */
    public void hideWaitDialog() {
        waitDialog.dismiss();
    }
}
