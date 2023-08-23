using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using UnityEngine;

public class Adapter
{
    
    private AndroidJavaClass unityClass;
    private AndroidJavaObject unityActivity;
    private AndroidJavaClass customClass;
    private AndroidJavaObject ble_instance;

    private const string PackageName = "com.zody.bleplugin.BlePlugin";
    private const string UnityDefaultJavaClassName = "com.unity3d.player.UnityPlayer";


    public void Init()
    {

        AndroidJNI.AttachCurrentThread();


        unityClass = new AndroidJavaClass(UnityDefaultJavaClassName);
        unityActivity = unityClass.GetStatic<AndroidJavaObject>("currentActivity");
        customClass = new AndroidJavaClass(PackageName);
        customClass.CallStatic("InitInstance", unityActivity);
        ble_instance = customClass.GetStatic<AndroidJavaObject>("instance");

        string plugin_name_version = ble_instance.Get<string>("PLUGIN_NAME");
        UnityEngine.Debug.Log(plugin_name_version);

        

    }



    private const int SCAN_PERIOD = 5;



    public List<string> GetDevices()
    {
        

        List<string> devices = new List<string>();
        string device = null;
        //ble_instance.Call("SetScanSettings", 2, 2, 1, 1);

        ble_instance.Call("ScanDevices");

        

        for (int i = 0; i < SCAN_PERIOD * 1000; i++)
        {
            Thread.Sleep(1);
        }
        
        ble_instance.Call("StopScanDevices");

        

        do
        {
            device = ble_instance.Call<string>("GetScanDevices");

            if (device != "")
            {
                devices.Add(device);
            }
            else
            {
                break;
            }
        }
        while (device != "");

        return devices;


    }


    public bool ConnectTo(BluetoothDevice device)
    {
        AndroidJNI.AttachCurrentThread();

       
        var res = ble_instance.Call<bool>("ConnectTo", device.address);


        return res;

        
    }


    public int GetConnectionState(BluetoothDevice device)
    {

        AndroidJNI.AttachCurrentThread();


        var res = ble_instance.Call<int>("CheckConnectionState", device.address);


        return res;

    }



    public Response SendTo(BluetoothDevice device, string value)
    {

        // Start write Characteristic
        string answer = ble_instance.Call<string>("WriteCharacteristic", device.address, value, 2);

        var response = JsonUtility.FromJson<Response>(answer);

        if (response.status == 1)
        {

            // wait callback On Write Characteristic
            response = GetResponse(device, 4000);

            response = JsonUtility.FromJson<Response>(answer);

            return response;
        }

        return response;


    }

    public Response ReadFrom(BluetoothDevice device)
    {

        
        // wait callback On Characteristic Change
        var response = GetReadFrom(device, 1500);

        //response = JsonUtility.FromJson<Response>(answer);


        //UnityEngine.Debug.Log(response.values);

        return response;

    }



    public Response SetNotifications(BluetoothDevice device)
    {
        string answer = ble_instance.Call<string>("SetNotifications", device.address);

        Response response = JsonUtility.FromJson<Response>(answer);

        if(response.status == 1)
        {

            // wait callback On start notifications
            response = GetResponse(device, 4000);

            response = JsonUtility.FromJson<Response>(answer);

            return response;
        }

        return response;


    }



    public Response GetResponse(BluetoothDevice device, int timeout)
    {

        Response resp = new Response();
        
        DateTime startTimeRead;

        

        startTimeRead = DateTime.Now;

        TimeSpan TimeOutRead;

        

        TimeOutRead = TimeSpan.FromMilliseconds((double)timeout);

        
        bool is_FailTimeout = false;

        string answerString = "";
        try
        {
            
            do
            {
                answerString = ble_instance.Call<string>("GetResponse", device.address);
                // Wait Response from plugin
                Thread.Sleep(50);
                if (TimeOutRead <= DateTime.Now - startTimeRead)
                {
                    //Timeout
                    UnityEngine.Debug.Log("Timeout");
                    is_FailTimeout = true;
                    break;
                }
            }
            while (answerString == "");
            //UnityEngine.Debug.Log(answerString);


        }
        catch (Exception ex)
        {
            resp.status = 4;
            //UnityEngine.Debug.Log("exception " + ex.Message);
            return resp;
        }

        

        //if it was TIMEOUT
        if (is_FailTimeout)
        {
            resp.status = -1;
            
            return resp;
        }

        resp = JsonUtility.FromJson<Response>(answerString);


        return resp;

    }

     
    public Response GetReadFrom(BluetoothDevice device, int timeout)
    {
        Response resp = new Response();

        DateTime startTimeRead;



        startTimeRead = DateTime.Now;

        TimeSpan TimeOutRead;



        TimeOutRead = TimeSpan.FromMilliseconds((double)timeout);


        bool is_FailTimeout = false;

        string answerString = "";
        try
        {

            do
            {
                answerString = ble_instance.Call<string>("GetReadCharacteristic", device.address);
                // Wait Response from plugin
                Thread.Sleep(50);
                if (TimeOutRead <= DateTime.Now - startTimeRead)
                {
                    //Timeout
                    UnityEngine.Debug.Log("Timeout");
                    is_FailTimeout = true;
                    break;
                }
            }
            while (answerString == "");
            //UnityEngine.Debug.Log(answerString);


        }
        catch (Exception ex)
        {
            resp.status = 4;
            //UnityEngine.Debug.Log("exception " + ex.Message);
            return resp;
        }



        //if it was TIMEOUT
        if (is_FailTimeout)
        {
            resp.status = -1;

            return resp;
        }

        resp = JsonUtility.FromJson<Response>(answerString);


        return resp;
    }


    public void SetScanFilters(string[] names)
    {
        ble_instance.Call("SetScanFilters", names);
    }



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
        ble_instance.Call("SetScanSettings", SCANMODE,CALLBACKTYPE,MATCHMODE,MATCHNUM);
    }



}
