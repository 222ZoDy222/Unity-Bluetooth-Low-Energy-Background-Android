package com.zody.bleplugin.JsonParser;

import android.bluetooth.BluetoothDevice;

import com.google.gson.Gson;
import com.zody.bleplugin.BleObject;
import com.zody.bleplugin.BlePlugin;

import com.google.gson.*;
import com.zody.bleplugin.Container.BTDevice;
import com.zody.bleplugin.Container.Response;

import java.io.IOException;


public class JsonParser
{


    public static String ToJson(BTDevice device)
    {

        String jsonStr;
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            jsonStr = gson.toJson(device);
        } catch (Exception e) {
            BlePlugin.UnityLog(e.getMessage());
            return null;
        }

        BlePlugin.UnityLog(jsonStr);

        return jsonStr;
    }



    public static String ToJson(Response response)
    {

        String jsonStr;
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            jsonStr = gson.toJson(response);
        } catch (Exception e) {
            BlePlugin.UnityLog(e.getMessage());
            return null;
        }

        //BlePlugin.UnityLog(jsonStr);

        return jsonStr;
    }








}
