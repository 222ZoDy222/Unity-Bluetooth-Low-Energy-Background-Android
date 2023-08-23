package com.zody.bleplugin.Scanning;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.zody.bleplugin.BlePlugin;

import java.util.ArrayList;
import java.util.List;


public class Ble_Scanner {

    private static BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothLeScanner mBluetoothLeScanner = null;

    public boolean mScanning = false;
    private Handler handler = new Handler();

    public void Init() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBluetoothAdapter.enable();

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();


    }

    public Ble_Scanner(){
        Init();
    }


    // region SCAN SETTINGS
    private ScanSettings m_scanSettings = null;

    private List<ScanFilter> m_scanFilters = null;

    /**
     * SCAN SETTINGS PARAMETERS
     * https://developer.android.com/reference/android/bluetooth/le/ScanSettings
     *
     * SCANMODE:
     *          SCAN_MODE_BALANCED : 1
     *          SCAN_MODE_LOW_LATENCY : 2
     *          SCAN_MODE_LOW_POWER : 0
     *          SCAN_MODE_OPPORTUNISTIC : -1
     *
     * Callback Type
     *          CALLBACK_TYPE_ALL_MATCHES : 1
     *          CALLBACK_TYPE_FIRST_MATCH : 2
     *          CALLBACK_TYPE_MATCH_LOST : 4
     *
     * MatchMode
     *          MATCH_MODE_AGGRESSIVE : 1
     *          MATCH_MODE_STICKY : 2
     *
     * MatchNum
     *          MATCH_NUM_FEW_ADVERTISEMENT : 2
     *          MATCH_NUM_MAX_ADVERTISEMENT : 3
     *          MATCH_NUM_ONE_ADVERTISEMENT : 1
     */
    public void SetScanSettings(int SCANMODE, int CALLBACKTYPE, int MATCHMODE, int MATCHNUM)
    {
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(SCANMODE)
                .setCallbackType(CALLBACKTYPE)
                .setMatchMode(MATCHMODE)
                .setNumOfMatches(MATCHNUM)
                .setReportDelay(0L)
                .build();
        m_scanSettings = scanSettings;
    }

    public void SetScanFilters(String[] names)
    {
        List<ScanFilter> filters = null;
        if(names != null) {
            filters = new ArrayList<>();
            for (String name : names) {
                ScanFilter filter = new ScanFilter.Builder()
                        .setDeviceName(name)
                        .build();
                filters.add(filter);
            }
        }

        m_scanFilters = filters;


    }


    public void DeleteScanSettings(){
        m_scanSettings = null;
    }


    // endregion

    public void scanBleDevices() {
        if (!mScanning) {
            foundDevices.clear();

            mScanning = true;
            if(m_scanSettings != null || m_scanFilters != null){
                unityLog("Scan with Settings");
                mBluetoothLeScanner.startScan(m_scanFilters,m_scanSettings,bleScanCallback);
            } else {
                unityLog("Scan without settings");
                mBluetoothLeScanner.startScan(bleScanCallback);
            }


            unityLog("Starting Scan");

            return;
        } else {
            unityLog("BLE Manager is already scanning.");
        }
    }



    private ArrayList<BluetoothDevice> foundDevices = new ArrayList<>();
    private ArrayList<BluetoothDevice> allDevices = new ArrayList<>();

    public BluetoothDevice GetFoundDevices(){
        if(foundDevices.size() == 0){
            return null;
        }

        BluetoothDevice device = foundDevices.get(0);
        foundDevices.remove(0);
        return device;


    }

    private ScanCallback bleScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();

                    if(!AlreadyFound(device,allDevices)){
                        allDevices.add(device);
                        foundDevices.add(device);
                    }


                    unityLog("Find device " + device.getName() + " " + device.getAddress());
                }
            };


    private boolean AlreadyFound(BluetoothDevice device, ArrayList<BluetoothDevice> deviceArrayList){
        for(int i = 0; i <= deviceArrayList.size()-1; i++){

            if(deviceArrayList.get(i).getAddress().equals(device.getAddress())){
                return true;
            }
        }
        return false;
    }

    public BluetoothDevice GetDevice(String address){
        for(int i = 0; i <= allDevices.size()-1; i++){

            String addressDevice = allDevices.get(i).getAddress().toUpperCase();
            String addressCurrent = address.toUpperCase();
            BlePlugin.UnityLog(addressDevice + " == " + addressCurrent + " is " + (addressDevice.equals(addressCurrent)));
            if(addressDevice.equals(addressCurrent))
            {
                return allDevices.get(i);
            }
        }
        return null;
    }


    public void stopScanBleDevices() {
        if (mScanning) {
            mBluetoothLeScanner.stopScan(bleScanCallback);
            mScanning = false;
        }
    }



    private static void unityLog(String s)
    {

        Log.i("Ble_Scanner",s);

    }




}
