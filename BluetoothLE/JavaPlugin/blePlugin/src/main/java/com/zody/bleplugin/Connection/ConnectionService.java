package com.zody.bleplugin.Connection;


import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_INDICATE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_SIGNED;

import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zody.bleplugin.BlePlugin;
import com.zody.bleplugin.Container.Response;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class ConnectionService {





    private BluetoothGatt m_currentBluetoothGatt;
    private BluetoothDevice m_currentBluetoothDevice;

    private BluetoothGattCharacteristic m_gattCharacteristicToWriteRead;
    /*
    STATE_CONNECTED = 2;
    STATE_CONNECTING = 1;
    STATE_DISCONNECTED = 0;
     */
    public int connectionState = 0;


    public String GetMacAddress() {
        return m_currentBluetoothDevice.getAddress();
    }

    public Response response = null;


    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String ReadValue = null;

    public ConnectionService(BluetoothDevice device, Context context, boolean autoConnect) {
        m_currentBluetoothDevice = device;
        response = new Response();
        response.responseID = 1;

        Connect(m_currentBluetoothDevice, context, autoConnect);
    }


    public void Connect(BluetoothDevice device, Context context, boolean autoConnect) {
        BlePlugin.UnityLog("TRY Connect to device - " + device.getAddress());
        m_currentBluetoothGatt = device.connectGatt(context, autoConnect, gattCallback, BluetoothDevice.TRANSPORT_LE);
    }



    private final String CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    private final String CHARACTERISTIC_READ_WRITE_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private BluetoothGattCharacteristic GetCharacteristic()
    {
        if(m_gattCharacteristicToWriteRead != null) return m_gattCharacteristicToWriteRead;

        for (int i = 0; i < m_currentBluetoothGatt.getServices().size(); i++)
        {
            BluetoothGattService service = m_currentBluetoothGatt.getServices().get(i);
            for (int j = 0; j < service.getCharacteristics().size(); j++)
            {
                BluetoothGattCharacteristic characteristic = service.getCharacteristics().get(j);

                if(characteristic.getUuid().equals(UUID.fromString(CHARACTERISTIC_READ_WRITE_UUID))){
                    m_gattCharacteristicToWriteRead = characteristic;
                    return m_gattCharacteristicToWriteRead;
                }


            }
        }
        BlePlugin.UnityLog("can't find Characteristic");
        for (int i = 0; i < m_currentBluetoothGatt.getServices().size(); i++)
        {

            BluetoothGattService service = m_currentBluetoothGatt.getServices().get(i);
            BlePlugin.UnityLog("service - " + service.getUuid());
            for (int j = 0; j < service.getCharacteristics().size(); j++)
            {
                BluetoothGattCharacteristic characteristic = service.getCharacteristics().get(j);
                BlePlugin.UnityLog("characteristic - " + characteristic.getUuid());
            }
        }
        return null;


    }


    private Response getResponse() {
        if (response == null) {
            response = new Response();
        }
        return response;
    }

    public void ClearResponse() {
        response = null;
    }


    public Response WriteCharacteristic(String bytes, int writeType) {
        //BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic()
        List<BluetoothGattService> services = m_currentBluetoothGatt.getServices();


        BluetoothGattCharacteristic characteristic = GetCharacteristic();

        characteristic.setValue(bytes);
        characteristic.setWriteType(WRITE_TYPE_DEFAULT );

        Response res = new Response();

        // Response Write
        res.responseID = 5;

        if (!m_currentBluetoothGatt.writeCharacteristic(characteristic)) {
            // Write characteristic error
            res.error = 5;

        } else {

            res.status = 1;
        }


        return res;


    }








    public String GetReadCharacteristic(){

        if(ReadValue == null) return null;
        String newValue = String.valueOf(ReadValue);
        ReadValue = null;
        return newValue;

    }


    //private final String CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    public Response SetNotifications(final boolean enable)
    {

        List<BluetoothGattService> services = m_currentBluetoothGatt.getServices();
        BluetoothGattCharacteristic characteristic = GetCharacteristic();
        Response res = new Response();

        res.responseID = 3;

        // Get the CCC Descriptor for the characteristic
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);

        if(descriptor == null) {
            //Log.e(TAG, String.format("ERROR: Could not get CCC descriptor for characteristic %s", characteristic.getUuid()));
            BlePlugin.UnityLog("ERROR: Could not get CCC descriptor for characteristic");
            res.status = 2;
            res.error = 9;
            return res;
        }
        // Check if characteristic has NOTIFY or INDICATE properties and set the correct byte value to be written
        byte[] value;
        int properties = characteristic.getProperties();
        if ((properties & PROPERTY_NOTIFY) > 0) {
            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
        } else if ((properties & PROPERTY_INDICATE) > 0) {
            value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
        } else {

            return null;
        }
        final byte[] finalValue = enable ? value : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;


        BlePlugin.UnityLog("value Notifications: " + finalValue);
        // Then write to descriptor
        descriptor.setValue(finalValue);


        m_currentBluetoothGatt.setCharacteristicNotification(characteristic,true);
        boolean result;
        result = m_currentBluetoothGatt.writeDescriptor(descriptor);
        if(!result) {
            BlePlugin.UnityLog("ERROR: writeDescriptor failed");
            res.status = 2;
            res.error = 11;
            return res;
        } else {
            res.status = 1;
            return res;
        }

    }

    public final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {


            BlePlugin.UnityLog("onConnectionStateChange - " + status + " state = " + newState);
            connectionState = newState;
            if(status == GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {


                    // Connection Completed
                    gatt.discoverServices();
                    //should wait services


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                    // Disconnection completed
                    gatt.close();

                }
            } else {
                // Error, something wrong;
                getResponse().error = 1;
                getResponse().status = 2;
                getResponse().errorMessage = "Something wrong in -onConnectionStateChange-";
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BlePlugin.UnityLog("onServicesDiscovered");

            if (status == GATT_SUCCESS) {

                final List<BluetoothGattService> services = gatt.getServices();

                getResponse().status = 1;
                //BlePlugin.instance.discoveredService(gatt);
            } else {
                getResponse().status = 2;
                getResponse().error = 3;
                getResponse().errorMessage = "Services Discovered Error";
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,final BluetoothGattCharacteristic characteristic) {
            //BlePlugin.UnityLog("onCharacteristicChanged");
            byte[] data = characteristic.getValue();
            String dataString = characteristic.getStringValue(0);
            //BlePlugin.UnityLog("Data Changed = " + dataString);

            ReadValue = dataString;
            //getResponse().status = 1;
            //getResponse().values = dataString;
            //mUnityAndroidBLE.characteristicValueChanged(gatt, characteristic);
            //getResponse().status = 1;
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {

            super.onCharacteristicRead(gatt, characteristic, status);

            BlePlugin.UnityLog("onCharacteristicRead - " + status);
            BlePlugin.UnityLog("BluetoothGattCharacteristic - " + characteristic.getUuid());
            byte[] data = characteristic.getValue();
            BlePlugin.UnityLog("onCharacteristicRead Data Count - " + data.length);
            String dataString = characteristic.getStringValue(0);




            BlePlugin.UnityLog("Data Read = " + dataString);



        }

        @Override
        public void onCharacteristicRead (BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          byte[] value,
                                          int status)
        {
            BlePlugin.UnityLog("onCharacteristicRead 2 with bytes");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            BlePlugin.UnityLog("onDescriptorWrite status = " + status);

            getResponse().status = 1;

        }


        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt,
                                   BluetoothGattCharacteristic characteristic,
                                   int status)
        {
            //BlePlugin.UnityLog("onCharacteristicWrite status = " + status);
            byte[] data = characteristic.getValue();
            String dataString = null;
            try {
                dataString = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                BlePlugin.UnityLog(e.getMessage());
            }

            //BlePlugin.UnityLog("Data Write= " + dataString);

            getResponse().status = 1;

        }



    };



}
