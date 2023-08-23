using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;
using UnityEngine;

public class Test : MonoBehaviour
{

    public static Test instance;



    private void Awake()
    {
        if(instance != null)
        {
            Destroy(instance);
        }
        instance = this;
    }

    public IBle Ble { get; private set; }


    /// <summary>
    /// The Java library's BleManager hook.
    /// </summary>
    AndroidJavaObject _bleLibrary = null;

    private const string PLUGIN_NAME = "com.zody.bleplugin.BlePlugin";



    // Start is called before the first frame update
    void Start()
    {

        UnityThread.initUnityThread();
        Ble = InterfaceReciever.RecieveIConfForm(gameObject);

        Initialize();
        

    }


    public void Initialize()
    {
        Ble.Init(() =>
        {




        });
    }




    [SerializeField] private Transform container;
    [SerializeField] private DevicePrefab devicePrefab;


    public void Scan()
    {

        for (int i = 0; i < container.childCount; i++)
        {
            Destroy(container.GetChild(i).gameObject);
        }

        Ble.GetDevices((msg) =>
        {
            //Debug.Log(msg);
            if (msg == null) return;
            foreach (var devString in msg)
            {
                var dev = JsonUtility.FromJson<BluetoothDevice>(devString);
                if (dev != null)
                {
                    Instantiate(devicePrefab, container).Init(dev);
                }
                else
                {
                    Debug.LogError($"Device is null from json = {devString}");
                }
            }



        });
        
    }





}
