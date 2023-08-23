using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BluetoothDevice
{

    /**
     * friendly Bluetooth name of the remote device.
     */
    public string name;

    /**
     * hardware address of this BluetoothDevice.
     */
    public string address;

    public string alias;

    public BT_type type;

    public string[] Uuid;

    public enum BT_type
    {
        DEVICE_TYPE_UNKNOWN,
        DEVICE_TYPE_CLASSIC,
        DEVICE_TYPE_LE,
        DEVICE_TYPE_DUAL
    }
}



public class Response
{

    /// <summary>
    /// 
    /// -2 - Error software
    /// -1 - Timeout
    /// 
    /// 0 - unComplete
    /// 
    /// 1 - OK
    /// 2 - Error
    /// 
    /// </summary>
    public int status;


    public string values;


    /// <summary>
    ///  0 - no errors
    ///  1 - error connection
    /// </summary>
    public int error;

    public string errorMessage;

    /// <summary>
    /// 0 - undefined
    /// 1 - Response on connection
    /// </summary>
    public int responseID;

}