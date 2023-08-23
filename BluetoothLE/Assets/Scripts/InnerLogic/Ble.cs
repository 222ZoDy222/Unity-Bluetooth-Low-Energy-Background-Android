using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using UnityEngine;




public class Ble : IBle
{


    private Adapter adapter = new Adapter();
   



    public void Init(Action callback)
    {

        adapter.Init();

        callback?.Invoke();

    }

    

   

  


    
    public void GetDevices(Action<List<string>> callback)
    {
        AndroidJNI.AttachCurrentThread();
        // It's working fine!
        //adapter.SetScanSettings(2, 1, 1, 3);
        //adapter.SetScanFilters(new string[] { "SMCLO-4B7BDF5D-1030" });
        var devs = adapter.GetDevices();

        callback?.Invoke(devs);

    }

    public void ConnectTo(BluetoothDevice device, Action<bool> callback)
    {
        AndroidJNI.AttachCurrentThread();
        var res = adapter.ConnectTo(device);

        callback?.Invoke(res);


    }


    public void CheckConnection(BluetoothDevice device, Action<int> callback)
    {
        AndroidJNI.AttachCurrentThread();
        var res = adapter.GetConnectionState(device);
        callback?.Invoke(res);
    }


    

    public void SetNotification(BluetoothDevice device, Action<Response> callback)
    {
        AndroidJNI.AttachCurrentThread();
        var res = adapter.SetNotifications(device);

        callback?.Invoke(res);
    
    }



    public void WriteCharacteristic(BluetoothDevice device, string value, Action<Response> callback)
    {
        // "0000INFO\r\n"
        AndroidJNI.AttachCurrentThread();
        var res = adapter.SendTo(device, value);

        callback?.Invoke(res);
    }

    public void WriteReadCharacteristic(BluetoothDevice device, string value, Action<Response> callback)
    {
        // "0000INFO\r\n"
        value += "\r\n";
        AndroidJNI.AttachCurrentThread();
        var res = adapter.SendTo(device, value);

        res = adapter.ReadFrom(device);

        callback?.Invoke(res);
    }

    public void BackgoundTest(BluetoothDevice device, string value) 
    {
        AndroidJNI.AttachCurrentThread();
        value += "\r\n";
        bool testVar = false;
        while (true)
        {
            testVar = !testVar;


            Response res;

            if (testVar)
            {
                res = adapter.SendTo(device, value);

                res = adapter.ReadFrom(device);
            } else
            {
                res = adapter.SendTo(device, "Test error \r\n");
                res = adapter.ReadFrom(device);

            }

            if(res.status == -1)
            {
                var connectionResult = adapter.ConnectTo(device);

                if (connectionResult)
                {
                    adapter.SetNotifications(device);
                    
                }

            }
            

            Thread.Sleep(500);
        }


    }

}









