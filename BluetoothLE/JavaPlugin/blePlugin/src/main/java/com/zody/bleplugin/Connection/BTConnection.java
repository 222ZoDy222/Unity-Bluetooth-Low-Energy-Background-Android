package com.zody.bleplugin.Connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;


import com.zody.bleplugin.BlePlugin;
import com.zody.bleplugin.Container.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BTConnection {


    private Map<BluetoothDevice, ConnectionService> mConnectedServers = null;
    private Map<BluetoothDevice, BluetoothGatt> mLeGattServers = null;

    private ArrayList<ConnectionService> allConnections = new ArrayList<>();


    public void connectToDevice(BluetoothDevice device, Context appContext) {

        ConnectionService service = GetBT_Gatt(device.getAddress());

        if(service != null){
            service.Connect(device,appContext,true);
            return;
        }

        if (device != null && !mConnectedServers.containsKey(device)) {
            ConnectionService service1 = new ConnectionService(device, appContext,true);
            mConnectedServers.put(device, service1);
            allConnections.add(service1);
        }
        else
        {
            BlePlugin.UnityLog("BluetoothDevice hasn't been discovered yet");
        }
    }

    public BTConnection(){
        mLeGattServers = new HashMap<BluetoothDevice, BluetoothGatt>();
        mConnectedServers = new HashMap<BluetoothDevice, ConnectionService>();
    }

    public Response GetResponse(String macAddress)
    {
        ConnectionService service = GetBT_Gatt(macAddress);
        if(service == null) {
            BlePlugin.UnityLog("Service is null");
            return null;
        }
        return service.response;

    }

    public Response GetReadCharacteristic(String macAddress){

        ConnectionService service = GetBT_Gatt(macAddress);
        if(service == null) {
            BlePlugin.UnityLog("Service is null");
            return null;
        }
        String result = service.GetReadCharacteristic();

        if(result == null){
            return null;
        }
        Response res = new Response();
        res.status = 1;
        res.values = result;
        return res;
    }

    private ConnectionService GetBT_Gatt(String macAddress)
    {
        for (int i = 0; i < allConnections.size(); i++){
            if(allConnections.get(i).GetMacAddress().equals(macAddress)){
                return  allConnections.get(i);
            }
        }
        return null;
    }


    public int GetConnectionState(String macAddress)
    {
        ConnectionService service = GetBT_Gatt(macAddress);
        if(service == null) {
            BlePlugin.UnityLog("Service is null");
            return -1;
        }
        return service.connectionState;
    }

    public Response WriteCharacteristic(String macAddress,String bytes, int writeType)
    {
        ConnectionService service = GetBT_Gatt(macAddress);
        if(service == null) {
            BlePlugin.UnityLog("Service is null");
            Response res = new Response();

            // Status error, errorID = undefined device
            res.status = 2;
            res.error = 2;

            // Response write
            res.responseID = 5;
            return res;
        }
        Response res = service.WriteCharacteristic(bytes,writeType);

        return res;



    }




    public Response SetNotifications(String macAddress)
    {
        ConnectionService service = GetBT_Gatt(macAddress);
        if(service == null) {
            BlePlugin.UnityLog("Service is null");
            Response res = new Response();

            // Status error, errorID = undefined device
            res.status = 2;
            res.error = 2;

            return res;
        }
        Response res = service.SetNotifications(true);

        return res;

    }


    public void ClearResponse(String macAddress){
        ConnectionService service = GetBT_Gatt(macAddress);
        if(service != null) service.ClearResponse();
    }




}
