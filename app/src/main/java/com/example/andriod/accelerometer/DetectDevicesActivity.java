package com.example.andriod.accelerometer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;

public class DetectDevicesActivity extends AppCompatActivity {
    private static final String TAG = "DetectDevicesActivity";
    private HashMap<String, UsbDevice> deviceList;
    private Iterator<UsbDevice> deviceIterator;
    private AlertDialog deviceSelectDialog;
    private PendingIntent mPermissionIntent;
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private UsbManager mUsbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_devices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                detectDevices();
            }
        });
        registerPermissionAcceptReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unRegisterPermissionAcceptReceiver();
    }

    private void registerPermissionAcceptReceiver() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    private void unRegisterPermissionAcceptReceiver() {
        unregisterReceiver(mUsbReceiver);
    }

    private void detectDevices() {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (deviceList != null && deviceList.size() > 0) {
            showAccessoriesList(deviceList);
        }
        else
        {
            Snackbar.make(findViewById(R.id.content_detect_devices), "none device detected!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showAccessoriesList(HashMap<String, UsbDevice> deviceList) {
        if (deviceList != null && deviceList.size() > 0) {
            String[] items = new String[deviceList.size()];
            int i = 0;
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                items[i++] = device.getDeviceName();
            }

            createDialog(items);
        }
    }

    private void createDialog(final String[] items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_device)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        mUsbManager.requestPermission(deviceList.get(items[which]), mPermissionIntent);
                        dialog.dismiss();
                    }
                });
        deviceSelectDialog = builder.create();
        deviceSelectDialog.show();
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

}
