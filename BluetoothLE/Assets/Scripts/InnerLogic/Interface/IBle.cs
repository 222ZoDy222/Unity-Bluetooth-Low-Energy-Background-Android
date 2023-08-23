using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


public interface IBle
{

    /// <summary>
    /// Initialize singleton in plugin
    /// </summary>
    public void Init(Action callback);

    
    public void GetDevices(Action<List<string>> callback);

    public void ConnectTo(BluetoothDevice device, Action<bool> callback);


    public void CheckConnection(BluetoothDevice device, Action<int> callback);


    public void SetNotification(BluetoothDevice device, Action<Response> callback);


    public void WriteCharacteristic(BluetoothDevice device, string value, Action<Response> callback);

    void WriteReadCharacteristic(BluetoothDevice device, string value, Action<Response> callback);
    public void BackgoundTest(BluetoothDevice device, string value);
}