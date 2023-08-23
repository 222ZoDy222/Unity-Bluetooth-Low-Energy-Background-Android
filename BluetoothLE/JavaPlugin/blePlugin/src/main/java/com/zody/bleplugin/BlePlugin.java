package com.zody.bleplugin;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Arrays;
import java.util.Scanner;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACTIVITY_RECOGNITION;
import static androidx.core.app.ActivityCompat.requestPermissions;

import com.zody.bleplugin.Connection.BTConnection;
import com.zody.bleplugin.Container.BTDevice;
import com.zody.bleplugin.Container.Response;
import com.zody.bleplugin.JsonParser.JsonParser;
import com.zody.bleplugin.Scanning.Ble_Scanner;


public class BlePlugin
{



    public static BlePlugin instance;

    public final String PLUGIN_NAME = "Plugin ZoDy 0.0.9";

    private Activity unityActivity;

    private Context unityContext;


    private Ble_Scanner m_scanner;

    public static void InitInstance(Activity activity){

        Activity tempActivity = activity;
        Context tempContext = activity.getApplicationContext();

        instance = new BlePlugin(tempActivity,tempContext);

        Log.i("BlePlugin", "Init Instance");
    }


    public BlePlugin(Activity activity, Context context)
    {

        this.unityActivity = activity;
        this.unityContext = context;

        //checkPermissions(unityContext,unityActivity);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void checkPermissions(Context context, Activity activity) {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
    }


    //region Scan Devices

    private Ble_Scanner GetScanner(){
        if(instance.m_scanner == null){
            BlePlugin.UnityLog("Create new Scanner");
            instance.m_scanner = new Ble_Scanner();
        }
        return instance.m_scanner;
    }


    public void ScanDevices()
    {
        Ble_Scanner scanner = GetScanner();
        scanner.scanBleDevices();
    }

    public String GetScanDevices()
    {
        BlePlugin.UnityLog("Get scan devices");
        BluetoothDevice device = GetScanner().GetFoundDevices();



        if(device != null){
            BTDevice btDevice = new BTDevice(device);
            BlePlugin.UnityLog("Device not null " + device.getAddress());
            String res = JsonParser.ToJson(btDevice);
            return res;
        } else  {
            BlePlugin.UnityLog("Device is null");
            return "";
        }
    }

    public void StopScanDevices()
    {
        Ble_Scanner scanner = GetScanner();
        scanner.stopScanBleDevices();
    }


    public void SetScanSettings(int SCANMODE, int CALLBACKTYPE, int MATCHMODE, int MATCHNUM)
    {
        GetScanner().SetScanSettings(SCANMODE,CALLBACKTYPE,MATCHMODE,MATCHNUM);
    }

    public void SetScanFilters(String[] names){
        GetScanner().SetScanFilters(names);
    }

    //endregion


    public boolean ConnectTo(String address){

        BluetoothDevice device = GetScanner().GetDevice(address);
        if(device == null){
            BlePlugin.UnityLog("This device was never founded");
            return false;
        } else
        {
            GetBtConnection().connectToDevice(device,instance.unityContext);
            return true;
        }
    }

    public String GetResponse(String macAddress)
    {
        Response res = GetBtConnection().GetResponse(macAddress);
        if(res != null && res.status != 0){
            String jsonRes = JsonParser.ToJson(res);
            //BlePlugin.UnityLog("response = " + jsonRes);
            GetBtConnection().ClearResponse(macAddress);
            return jsonRes;
        } else  {
            //BlePlugin.UnityLog("response = null");
            return "";
        }

    }


    private BTConnection m_btConnection;
    private BTConnection GetBtConnection()
    {
        if(instance.m_btConnection == null){
            BlePlugin.UnityLog("Create new BTConnection");
            instance.m_btConnection = new BTConnection();
        }
        return instance.m_btConnection;
    }


    /**
     *
     * @param macAddress - address of bluetooth device to write bytes
     * @param bytes
     * @param writeType - Type of writeCharacteristic method
     *                  1 - WRITE_TYPE_NO_RESPONSE
     *                  2 - WRITE_TYPE_DEFAULT
     *                  4 - WRITE_TYPE_SIGNED
     * @return
     */
    public String WriteCharacteristic(String macAddress,String bytes, int writeType)
    {

        Response res = GetBtConnection().WriteCharacteristic(macAddress,bytes,writeType);
        GetBtConnection().ClearResponse(macAddress);
        if(res != null){
            String jsonRes = JsonParser.ToJson(res);

            return jsonRes;
        } else  {
            res = new Response();
            res.status = 2;
            res.error = 5;
            String jsonRes = JsonParser.ToJson(res);

            return jsonRes;
        }

    }


    public String GetReadCharacteristic(String macAddress)
    {
        //BlePlugin.UnityLog("GetReadCharacteristic");
        Response res = GetBtConnection().GetReadCharacteristic(macAddress);
        if(res != null){
            BlePlugin.UnityLog("GetReadCharacteristic = " + res.values);
            return  JsonParser.ToJson(res);
        } else {
            BlePlugin.UnityLog("GetReadCharacteristic = " + "-");
            return "";
        }
    }



    public String SetNotifications(String macAddress){
        BlePlugin.UnityLog("SetNotifications");
        Response res = GetBtConnection().SetNotifications(macAddress);
        GetBtConnection().ClearResponse(macAddress);
        if(res != null){
            String jsonRes = JsonParser.ToJson(res);
            BlePlugin.UnityLog(jsonRes);
            return jsonRes;
        } else  {
            res = new Response();
            res.status = 2;
            res.error = 8;
            String jsonRes = JsonParser.ToJson(res);
            BlePlugin.UnityLog(jsonRes);
            return jsonRes;
        }
    }


    public int CheckConnectionState(String macAddress)
    {
        return GetBtConnection().GetConnectionState(macAddress);
    }


    public static void UnityLog(String value){
        Log.i("Ble_Plugin",value);
    }


}

