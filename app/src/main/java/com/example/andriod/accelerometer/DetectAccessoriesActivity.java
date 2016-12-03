package com.example.andriod.accelerometer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class DetectAccessoriesActivity extends AppCompatActivity {
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    private static final String TAG = "DetectAccessoriesActivity";
    private PendingIntent mPermissionIntent;
    private UsbAccessory accessory;
    private UsbManager usbManager;
    private AlertDialog accessorySelectDialog;
    private  UsbAccessory[] allAccessories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startDetecting();
            }
        });
        registerPermissionAcceptReceiver();
    }

    private void registerPermissionAcceptReceiver() {
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    private void unRegisterPermissionAcceptReceiver() {
        unregisterReceiver(mUsbReceiver);
    }

    private void startDetecting() {

        allAccessories = usbManager.getAccessoryList();
        if (allAccessories == null || allAccessories.length == 0) {
            Snackbar.make(findViewById(R.id.content_detect), "none accessory detected!", Snackbar.LENGTH_SHORT).show();
        } else {
            showAccessoriesList(allAccessories);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        unRegisterPermissionAcceptReceiver();
    }

    private void showAccessoriesList(UsbAccessory[] accessories) {
        if (accessories != null && accessories.length > 0) {
            String[] items = new String[accessories.length];
            int i = 0;
            for (UsbAccessory accessory : accessories) {

                items[i++] = accessory.getModel();

            }
            createDialog(items);
        }
    }

    private void createDialog(String[] items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_accessory)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        usbManager.requestPermission(allAccessories[which], mPermissionIntent);
                        dialog.dismiss();
                    }
                });
        accessorySelectDialog = builder.create();
        accessorySelectDialog.show();
    }

    private void closeAccessoriesList() {
        if (accessorySelectDialog != null && accessorySelectDialog.isShowing()) {
            accessorySelectDialog.dismiss();
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (accessory != null) {
                            //call method to set up accessory communication
                            Log.d(TAG, "permission accepted for accessory " + accessory);
                        }
                    } else {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                    }
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null) {
                    // call your method that cleans up and closes communication with the accessory
                    Log.e(TAG, "accessory removed!" + accessory);
                }
            }
        }
    };
}
