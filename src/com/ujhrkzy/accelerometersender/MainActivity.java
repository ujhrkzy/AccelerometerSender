package com.ujhrkzy.accelerometersender;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    private final static int DEVICES_DIALOG = 1;
    private final static int ERROR_DIALOG = 2;
    // private final BluetoothTask bluetoothTask;
    private AccelerometerSensor accelerometerSensor;
    private ProgressDialog waitDialog;
    private EditText editTextX;
    private EditText editTextY;
    private EditText editTextZ;
    private String errorMessage = "";

    public MainActivity() {
        // this.bluetoothTask = new BluetoothTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextX = (EditText) findViewById(R.id.editText1);
        editTextY = (EditText) findViewById(R.id.editText2);
        editTextZ = (EditText) findViewById(R.id.editText3);
        List<AccelerometerEventListener> listeners = new ArrayList<AccelerometerEventListener>();
        AccelerometerEventListener listener = createViewEventListener(
                editTextX, editTextY, editTextZ);
        listeners.add(listener);
        // listeners.add(bluetoothTask.createAccelerometerEventListener());
        this.accelerometerSensor = new AccelerometerSensor(listeners);
        accelerometerSensor.onCreate((SensorManager) this
                .getSystemService(Context.SENSOR_SERVICE));

        Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometerSensor.reset();
                // bluetoothTask.doSend("reset");
            }
        });
    }

    private AccelerometerEventListener createViewEventListener(
            final EditText editTextX, final EditText editTextY,
            final EditText editTextZ) {
        return new AccelerometerEventListener() {

            @Override
            public void accept(AccelerometerValue value) {
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
        // bluetoothTask.init();
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
        // bluetoothTask.doClose();
        super.onDestroy();
    }

    protected void restart() {
        Intent intent = this.getIntent();
        this.finish();
        this.startActivity(intent);
    }

    // ----------------------------------------------------------------
    // 以下、ダイアログ関連
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DEVICES_DIALOG)
            // return createDevicesDialog();
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

    // public Dialog createDevicesDialog() {
    // AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    // alertDialogBuilder.setTitle("Select device");
    //
    // // ペアリング済みデバイスをダイアログのリストに設定する。
    // Set<BluetoothDevice> pairedDevices = bluetoothTask.getPairedDevices();
    // final BluetoothDevice[] devices = pairedDevices
    // .toArray(new BluetoothDevice[0]);
    // String[] items = new String[devices.length];
    // for (int i = 0; i < devices.length; i++) {
    // items[i] = devices[i].getName();
    // }
    //
    // alertDialogBuilder.setItems(items,
    // new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(DialogInterface dialog, int which) {
    // dialog.dismiss();
    // // 選択されたデバイスを通知する。そのまま接続開始。
    // bluetoothTask.doConnect(devices[which]);
    // }
    // });
    // alertDialogBuilder.setCancelable(false);
    // return alertDialogBuilder.create();
    // }

    @SuppressWarnings("deprecation")
    public void errorDialog(String msg) {
        if (this.isFinishing())
            return;
        this.errorMessage = msg;
        this.showDialog(ERROR_DIALOG);
    }

    public Dialog createErrorDialog() {
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

    public void showWaitDialog(String msg) {
        if (waitDialog == null) {
            waitDialog = new ProgressDialog(this);
        }
        waitDialog.setMessage(msg);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.show();
    }

    public void hideWaitDialog() {
        waitDialog.dismiss();
    }
}
