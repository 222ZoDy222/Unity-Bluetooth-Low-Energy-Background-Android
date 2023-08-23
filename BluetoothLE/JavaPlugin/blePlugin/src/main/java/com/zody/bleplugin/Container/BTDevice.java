package com.zody.bleplugin.Container;


import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.zody.bleplugin.BlePlugin;

public class BTDevice {
    /**
     * friendly Bluetooth name of the remote device.
     */
    public String name;

    /**
     * hardware address of this BluetoothDevice.
     */
    public String address;

    public String alias;

    public BT_type type;

    public String[] Uuid;

    public BTDevice(BluetoothDevice device) {
        try {
            this.name = device.getName();
        } catch (Exception ex){
            BlePlugin.UnityLog(ex.getMessage());
        }

        this.address = device.getAddress();
        this.alias = device.getAlias();
        this.type = BT_type.FromInteger(device.getType());
        android.os.ParcelUuid[] uuids = device.getUuids();
        if (uuids != null){
            BlePlugin.UnityLog("uuids NOT NULL");
            this.Uuid = new String[device.getUuids().length];
            for (int i = 0; i < device.getUuids().length; i++){
                this.Uuid[i] = device.getUuids()[i].getUuid().toString();
            }
        } else {
            BlePlugin.UnityLog("uuids is NULL");
        }


    }


    public enum BT_type{
        DEVICE_TYPE_UNKNOWN,
        DEVICE_TYPE_CLASSIC,
        DEVICE_TYPE_LE ,
        DEVICE_TYPE_DUAL;

        public static BT_type FromInteger(int value){
            switch(value) {
                case 0:
                    return DEVICE_TYPE_UNKNOWN;
                case 1:
                    return DEVICE_TYPE_CLASSIC;
                case 2:
                    return DEVICE_TYPE_LE;
                case 3:
                    return DEVICE_TYPE_DUAL;
            }
            return null;
        }

    }

}
