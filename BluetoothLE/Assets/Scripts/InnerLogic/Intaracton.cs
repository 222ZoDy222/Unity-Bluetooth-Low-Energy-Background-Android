using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UnityEngine;
using System.Threading;


/// <summary>
/// Класс, который связывает верхнюю часть приложения с низким уровнем, 
/// низкий уровень выполняется в новом потоке
/// </summary>
public class Intaracton : MonoBehaviour, IBle
{
    private IBle ble;

    
    public Intaracton Initialize()
    {
        ble = new Ble();
        return this;
    }


    public void Init(Action callback)
    {
        new Thread(() =>
        {
            ble.Init(()=> 
            {
                UnityThread.executeInUpdate(() =>
                {                   
                    callback?.Invoke();
                });
            });
        }).Start();

    }

    public void GetDevices(Action<List<string>> callback)
    {
        new Thread(() =>
        {
            ble.GetDevices((msg) =>
            {
                UnityThread.executeInUpdate(() =>
                {
                    callback?.Invoke(msg);
                });
            });
        }).Start();
    }

    

    
    public void ConnectTo(BluetoothDevice device, Action<bool> callback)
    {
        new Thread(() =>
        {
            ble.ConnectTo(device,(msg) =>
            {
                UnityThread.executeInUpdate(() =>
                {
                    
                    callback?.Invoke(msg);
                });
            });
        }).Start();
    }

    public void CheckConnection(BluetoothDevice device, Action<int> callback)
    {
        new Thread(() =>
        {
            ble.CheckConnection(device, (msg) =>
            {
                UnityThread.executeInUpdate(() =>
                {

                    callback?.Invoke(msg);
                });
            });
        }).Start();
    }

    public void SetNotification(BluetoothDevice device, Action<Response> callback)
    {
        new Thread(() =>
        {
            ble.SetNotification(device, (msg) =>
            {
                UnityThread.executeInUpdate(() =>
                {

                    callback?.Invoke(msg);
                });
            });
        }).Start();
    }
    public void WriteCharacteristic(BluetoothDevice device, string value, Action<Response> callback)
    {
        new Thread(() =>
        {
            ble.WriteCharacteristic(device, value, (msg) =>
            {
                UnityThread.executeInUpdate(() =>
                {

                    callback?.Invoke(msg);
                });
            });
        }).Start();
    }

    public void WriteReadCharacteristic(BluetoothDevice device, string value, Action<Response> callback)
    {
        new Thread(() =>
        {
            ble.WriteReadCharacteristic(device, value, (msg) =>
            {
                UnityThread.executeInUpdate(() =>
                {

                    callback?.Invoke(msg);
                });
            });
        }).Start();
    }

    public void BackgoundTest(BluetoothDevice device, string value)
    {
        new Thread(() =>
        {
            ble.BackgoundTest(device, value);
        }).Start();
    }

}



