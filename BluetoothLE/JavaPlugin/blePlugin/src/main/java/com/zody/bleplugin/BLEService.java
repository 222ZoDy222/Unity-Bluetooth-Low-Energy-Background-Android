package com.zody.bleplugin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by QT on 2016/7/7.
 */
public class BLEService extends Service
{
    private static final String TAG = BLEService.class.getSimpleName();


    private static final int NOTIFICATION_BG_ID = 99;

    private String KEY_WORD_LOCK = "lock";
    private String KEY_WORD_UNLOCK = "asdfghj";
    private String GATT_UUID_SERVICE = "A5B288C3-FC55-491F-AF38-27D2F7D7BF25";
    private String GATT_UUID_CHARACTERS_UUID = "A6282AC7-7FCA-4852-A2E6-1D69121FD44A";
    private String GATT_ADDRESS = "60:F8:1D:B9:FA:BF";

    private List<ScanFilter> mLeFilter;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothGatt mBlutoothGatt = null;

    private BluetoothDevice mLeDevice = null;
    private BluetoothGattCharacteristic mBluetoothLeCharacteristic = null;
    private BluetoothLeScanner mBluetoothLeScanner = null;


    public static final int INTENT_REQ_EXIT_SERVICE = 88;
    public static final int INTENT_REQ_UNLOCK = 89;
    public static final int INTENT_REQ_LOCK = 90;
    public static final int INTENT_UPDATE_UNLOCK_INFO = 99;

    public static final String PERFRENCE_KEY = "KEY";
    public static final String PERFRENCE_KEY_LOCK = "KEYL";
    public static final String PERFRENCE_ADDRESS = "ADDRESS";

    private int lastIntentReq = -1;
    private boolean bForeground = false;
    private boolean bUsingFingerprint = false;
    private Boolean m_BshowExitBtn = true;
    private Boolean m_BshowLockBtn = true;
    private Boolean m_BshowUnlockBtn = true;
    //private STATE state;

    private SharedPreferences mSharedPreferences;
    private RemoteViews ctlViews;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //BlePlugin.countDevices += 100;
        }




    };






    private void startscanDevice(){
        if(mLeDevice != null){
            return;
        }
        //Log.i(TAG, GATT_ADDRESS);
        //updateState(STATE.SCANNING);
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mLeFilter.clear();
        mLeFilter.add(new ScanFilter.Builder().
                setDeviceAddress(GATT_ADDRESS)
                .build());
        new Thread(){
            @Override
            public void run() {
                Log.i(TAG,"BluetoothLeScanner Start Scan");


                mBluetoothLeScanner.startScan(mLeFilter,
                        new ScanSettings.Builder().
                                setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).
                                setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).
                                build(),
                        mDeviceScanCallback);
            }
        }.run();

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLeFilter = new ArrayList<ScanFilter>();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

        startscanDevice();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mBTAReceiver);
    }




    private ScanCallback mDeviceScanCallback = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);


        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            //BlePlugin.countDevices++;
            mLeDevice = result.getDevice();
            mReceiver.setResultData("0");

        }
        @Override
        public void onBatchScanResults(List< android.bluetooth.le.ScanResult > results) {
            super.onBatchScanResults(results);

        }
    };
    private final BroadcastReceiver mBTAReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (btState){
                    case BluetoothAdapter.STATE_ON:
                        //updateState(STATE.BT_ON);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        //updateState(STATE.BT_OFF);
                        break;
                }
            }
        }
    };


}